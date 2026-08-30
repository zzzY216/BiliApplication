package com.software.home.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.software.core.data.repository.BangumiRepository
import com.software.core.model.pgc.PgcIndexItem
import com.software.core.model.pgc.PgcRankItem
import com.software.core.model.pgc.TimelineResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 动漫 Tab 页面状态（排行/时间线一次性数据） */
data class AnimeUiState(
    val rank: List<PgcRankItem> = emptyList(),
    val rankLoading: Boolean = false,
    val rankError: String? = null,
    val timeline: List<TimelineResult> = emptyList(),
    val timelineLoading: Boolean = false,
    val timelineError: String? = null,
)

/**
 * 动漫 Tab ViewModel：索引走分页流（cachedIn），排行/时间线兜底用 StateFlow + retry。
 */
@HiltViewModel
class AnimeViewModel @Inject constructor(
    private val bangumiRepository: BangumiRepository,
) : ViewModel() {

    companion object {
        const val SEASON_TYPE_BANGUMI = 1      // 番剧
        const val SEASON_TYPE_GUOCHUANG = 4    // 国创
    }

    /** 番剧索引分页流（推荐子页签主内容，番剧） */
    val indexPagingData: Flow<PagingData<PgcIndexItem>> =
        bangumiRepository.getIndexPagingFlow(seasonType = SEASON_TYPE_BANGUMI)
            .cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(AnimeUiState())
    val uiState: StateFlow<AnimeUiState> = _uiState.asStateFlow()

    init {
        loadRank()
        loadTimeline()
    }

    fun loadRank() {
        viewModelScope.launch {
            _uiState.update { it.copy(rankLoading = true, rankError = null) }
            bangumiRepository.getRank(seasonType = SEASON_TYPE_BANGUMI, day = 3)
                .onSuccess { list ->
                    _uiState.update { it.copy(rank = list) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(rankError = e.message) }
                }
            _uiState.update { it.copy(rankLoading = false) }
        }
    }

    fun loadTimeline() {
        viewModelScope.launch {
            _uiState.update { it.copy(timelineLoading = true, timelineError = null) }
            bangumiRepository.getTimeline(types = SEASON_TYPE_BANGUMI, before = 7, after = 7)
                .onSuccess { list ->
                    _uiState.update { it.copy(timeline = list) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(timelineError = e.message) }
                }
            _uiState.update { it.copy(timelineLoading = false) }
        }
    }
}