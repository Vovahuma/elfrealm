package com.elf.real.elf.game

class GameLogic {
    companion object {
        fun checkForVictory(s: String, boardList: List<ButtonObject>): Boolean {
            //Horizontal Victory
            if (match(boardList[0], s) && match(boardList[1], s) && match(boardList[2], s))
                return true
            if (match(boardList[3], s) && match(boardList[4], s) && match(boardList[5], s))
                return true
            if (match(boardList[6], s) && match(boardList[7], s) && match(boardList[8], s))
                return true

            //Vertical Victory
            if (match(boardList[0], s) && match(boardList[3], s) && match(boardList[6], s))
                return true
            if (match(boardList[1], s) && match(boardList[4], s) && match(boardList[7], s))
                return true
            if (match(boardList[2], s) && match(boardList[5], s) && match(boardList[8], s))
                return true

            //Diagonal Victory
            if (match(boardList[0], s) && match(boardList[4], s) && match(boardList[8], s))
                return true
            if (match(boardList[2], s) && match(boardList[4], s) && match(boardList[6], s))
                return true

            return false
        }

        private fun match(button: ButtonObject, s: String): Boolean = button.text.equals(s)

    }
}