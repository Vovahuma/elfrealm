package com.elf.real.me.game.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elf.real.me.R
import com.elf.real.me.game.ButtonObject
import com.elf.real.me.game.GameLogic
import com.elf.real.me.game.Turn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val boardList: List<ButtonObject>,
    private val isSinglePlayer: Boolean
) :
    ViewModel() {
    private var currentTurn = Turn.CROSS
    private val nought = "O"
    private val cross = "X"

    private var _turn = MutableStateFlow(String())
    var turn: StateFlow<String> = _turn

    private var _result = MutableStateFlow(String())
    var result: StateFlow<String> = _result

    private fun fullBoard(): Boolean {
        for (button in boardList) {
            if (button.text == "")
                return false
        }
        return true
    }

    fun addToBoard(button: ButtonObject) {
        if (button.text != "")
            return
        if (currentTurn == Turn.NOUGHT) {
            button.text = nought
            button.button.setBackgroundResource(R.drawable.nought)
            currentTurn = Turn.CROSS
        } else if (currentTurn == Turn.CROSS) {
            button.text = cross
            button.button.setBackgroundResource(R.drawable.cross)
            currentTurn = Turn.NOUGHT
        }
        setTurnLabel()
        checkResult()
    }

    private fun checkResult() {
        if (GameLogic.checkForVictory(nought, boardList)) {
            _result.value = "Noughts Win!"
        } else if (GameLogic.checkForVictory(cross, boardList)) {
            _result.value = "Crosses Win!"
        }

        if (fullBoard()) {
            _result.value = "Draw"
        }
    }

    private fun setTurnLabel() {
        var turnText = ""
        if (currentTurn == Turn.CROSS) {
            turnText = "Turn $cross"
        } else if (currentTurn == Turn.NOUGHT) {
            turnText = "Turn $nought"
        }

        _turn.value = turnText
        if (currentTurn == Turn.NOUGHT && !fullBoard() && isSinglePlayer) {
            robot()
        }

    }

    private fun robot() {
        var random: Int
        do {
            viewModelScope.launch(Dispatchers.Main) {
                delay(200)
            }
            val values = (0..8).toList()
            random = values.random()
        } while (boardList[random].text != "")
        addToBoard(boardList[random])
    }
}