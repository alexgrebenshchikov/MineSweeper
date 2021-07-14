class MineSweeper(private val size: Int, private val nMines: Int) {

    private val safeCell = '.'
    private val mineCell = 'X'
    private val markedCell = '*'
    private val exploredCell = '/'
    private val field = Array(size) { Array(size) { safeCell to CellStatus.UNKNOWN } }
    private var markedCount = 0
    private var rightMarkedCount = 0
    private var exploredCount = 0
    private var isFirstExplore = true


    enum class CellStatus {
        UNKNOWN,
        MARKED,
        EXPLORED
    }

    private fun initMines(i: Int, j: Int) {
        val indexes = (0 until size * size).toMutableSet()
        indexes.remove(i * size + j)
        repeat(nMines) {
            val pos = indexes.random()
            field[pos / 9][pos % 9] = mineCell to field[pos / 9][pos % 9].second
            if (field[pos / 9][pos % 9].second == CellStatus.MARKED)
                rightMarkedCount++
            indexes.remove(pos)
        }
    }

    private fun initHints() {
        field.forEachIndexed { i, row ->
            row.forEachIndexed { j, cell ->
                if (cell.first == safeCell) {
                    val n = checkNeighbours(i, j)
                    if (n > 0)
                        field[i][j] = ('0' + n) to field[i][j].second
                }
            }
        }
    }

    private fun checkNeighbours(i: Int, j: Int): Int {
        var cnt = 0

        (-1..1).forEach { im ->
            (-1..1).forEach { jm ->
                if (i + im in 0 until size && j + jm in 0 until size && field[i + im][j + jm].first == mineCell)
                    cnt++
            }
        }
        return cnt
    }

    fun setDeleteMark(j: Int, i: Int): Boolean {
        return when (field[i][j].second) {
            CellStatus.MARKED -> {
                field[i][j] = field[i][j].first to CellStatus.UNKNOWN
                markedCount--
                if (field[i][j].first == mineCell) rightMarkedCount--
                true
            }
            CellStatus.UNKNOWN -> {
                field[i][j] = field[i][j].first to CellStatus.MARKED
                markedCount++
                if (field[i][j].first == mineCell) rightMarkedCount++
                true
            }
            CellStatus.EXPLORED -> false
        }
    }

    fun explore(j: Int, i: Int): Boolean {
        if (isFirstExplore) {
            isFirstExplore = false
            initMines(i, j)
            initHints()
        }
        when (field[i][j].second) {
            CellStatus.UNKNOWN, CellStatus.MARKED -> {
                if (field[i][j].second == CellStatus.MARKED)
                    markedCount--
                when (field[i][j].first) {
                    mineCell -> return false
                    safeCell -> {
                        field[i][j] = field[i][j].first to CellStatus.EXPLORED
                        exploredCount++
                        (-1..1).forEach { im ->
                            (-1..1).forEach { jm ->
                                if (i + im in 0 until size && j + jm in 0 until size && (im != 0 || jm != 0))
                                    explore(j + jm, i + im)
                            }
                        }
                        return true
                    }
                    else -> {
                        field[i][j] = field[i][j].first to CellStatus.EXPLORED
                        exploredCount++
                        return true
                    }
                }
            }
            else -> return true
        }
    }


    fun showState(isFailed: Boolean = false) {
        println((1..size).joinToString("", " |", "|"))
        val prettySep = Array(size) { '-' }.joinToString("", "-|", "|")
        println(prettySep)
        field.forEachIndexed { i, row ->
            println(row.map {
                when (it.second) {
                    CellStatus.MARKED -> markedCell
                    CellStatus.UNKNOWN ->
                        if (isFailed && it.first == mineCell) mineCell else safeCell
                    CellStatus.EXPLORED -> when (it.first) {
                        safeCell -> exploredCell
                        else -> it.first
                    }
                }
            }.joinToString("", ('0' + i + 1) + "|", "|"))
        }
        println(prettySep)
    }

    fun isNotEnded(): Boolean =
        (markedCount != nMines || rightMarkedCount != nMines) && exploredCount != (size * size - nMines)
}