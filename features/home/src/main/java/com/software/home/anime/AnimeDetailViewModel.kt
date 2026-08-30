package com.software.home.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.software.core.data.repository.BangumiRepository
import com.software.core.model.pgc.PgcUserStatus
import com.software.core.model.pgc.SeasonDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 动漫详情页状态 */
data class AnimeDetailUiState(
    val season: SeasonDetail? = null,
    val loading: Boolean = true,
    val error: String? = null,
    val followToggling: Boolean = false,
)

/** 动漫详情页：剧集明细 + 追番 */
@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    private val bangumiRepository: BangumiRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnimeDetailUiState())
    val uiState: StateFlow<AnimeDetailUiState> = _uiState.asStateFlow()

    private var seasonId: Long = 0L

    /** 加载明细；同一条目已加载时幂等（避免重复请求） */
    fun load(seasonId: Long) {
        if (seasonId == this.seasonId && _uiState.value.season != null) return
        this.seasonId = seasonId
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            bangumiRepository.getSeasonDetail(seasonId)
                .onSuccess { detail ->
                    _uiState.update { it.copy(season = detail, loading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(loading = false, error = e.message) }
                }
        }
    }

    /** 追番 / 取消追番（乐观更新 userStatus.follow） */
    fun toggleFollow() {
        if (_uiState.value.followToggling) return
        val follow = _uiState.value.season?.userStatus?.follow == 1
        viewModelScope.launch {
            _uiState.update { it.copy(followToggling = true) }
            bangumiRepository.setFollow(seasonId, follow = !follow)
                .onSuccess {
                    _uiState.update { state ->
                        val season = state.season?.copy(
                            userStatus = PgcUserStatus(
                                follow = if (follow) 0 else 1,
                                followStatus = null
                            )
                        )
                        state.copy(season = season)
                    }
                }
            _uiState.update { it.copy(followToggling = false) }
        }
    }
}