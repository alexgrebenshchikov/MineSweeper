


fun main() {
    println("How many mines do you want on the field?")
    val nMines = readLine()!!.toInt()
    val game = MineSweeper(9, nMines)
    game.showState()
    var fail = false
    while(game.isNotEnded()) {
        println("Set/unset mine marks or claim a cell as free:")
        val (x, y, action) = readLine()!!.split(" ")
        val i = x.toInt()
        val j = y.toInt()
        when(action) {
            "mine" ->
                if(game.setDeleteMark(i - 1, j - 1))
                    game.showState()
                else
                    println("Cell already explored!")
            else ->
                if(game.explore(i - 1, j - 1))
                    game.showState()
                else {
                    game.showState(true)
                    println("You stepped on a mine and failed!")
                    fail = true
                    break
                }
        }

    }
    if(!fail)
        println("Congratulations! You found all the mines!")

}