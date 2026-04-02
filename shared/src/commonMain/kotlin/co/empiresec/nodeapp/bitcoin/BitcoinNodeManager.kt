package co.empiresec.nodeapp.bitcoin

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class BitcoinNodeManager {
    private val manager = BitcoinCoreManager()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    private val _nodeStatus = MutableStateFlow<NodeStatus?>(null)
    val nodeStatus: StateFlow<NodeStatus?> = _nodeStatus.asStateFlow()
    
    private val _blockChainInfo = MutableStateFlow<BlockChainInfo?>(null)
    val blockChainInfo: StateFlow<BlockChainInfo?> = _blockChainInfo.asStateFlow()
    
    private val _networkInfo = MutableStateFlow<NetworkInfo?>(null)
    val networkInfo: StateFlow<NetworkInfo?> = _networkInfo.asStateFlow()
    
    private val _mempoolInfo = MutableStateFlow<MempoolInfo?>(null)
    val mempoolInfo: StateFlow<MempoolInfo?> = _mempoolInfo.asStateFlow()
    
    private val _peers = MutableStateFlow<List<PeerInfo>>(emptyList())
    val peers: StateFlow<List<PeerInfo>> = _peers.asStateFlow()
    
    private var refreshJob: Job? = null
    
    fun startAutoRefresh(intervalMs: Long = 5000) {
        refreshJob?.cancel()
        refreshJob = scope.launch {
            while (isActive) {
                refreshStatus()
                delay(intervalMs)
            }
        }
    }
    
    fun stopAutoRefresh() {
        refreshJob?.cancel()
    }
    
    suspend fun refreshStatus() {
        try {
            val status = manager.getStatus()
            _nodeStatus.value = status
            
            if (status.state == DaemonState.RUNNING) {
                val client = BitcoinRPCClient(BitcoinConf.default())
                try {
                    _blockChainInfo.value = client.getBlockChainInfo()
                    _networkInfo.value = client.getNetworkInfo()
                    _mempoolInfo.value = client.getMempoolInfo()
                    _peers.value = client.getPeerInfo()
                } catch (e: Exception) {
                    // Daemon may not be fully ready
                }
            }
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    val isRunning: Boolean
        get() = _nodeStatus.value?.state == DaemonState.RUNNING
    
    fun cleanup() {
        stopAutoRefresh()
        scope.cancel()
    }
    
    companion object {
        val instance = BitcoinNodeManager()
    }
}