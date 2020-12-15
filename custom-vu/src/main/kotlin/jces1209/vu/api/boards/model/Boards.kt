package jces1209.vu.api.boards.model

import jces1209.vu.api.dashboard.model.Dashboard

data class Boards(
    val isLast: Boolean,
    val values: List<Board>
)
