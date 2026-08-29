package com.software.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.software.core.data.repository.VideoRepository
import com.software.core.model.RecommendItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class BiliHomeViewModel @Inject constructor(
    videoRepository: VideoRepository,
) : ViewModel() {

    /** 推荐流（Paging3）：由仓储提供，ViewModel 持有缓存，旋转/返回不重复请求 */
    val recommendPagingData: Flow<PagingData<RecommendItem>> =
        videoRepository.getRecommendVideoPagingFlow()
            .cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(BiliHomeUiState())
    val uiState = _uiState.asStateFlow()
}
