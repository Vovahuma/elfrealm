package com.elf.real.elf.game.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.elf.real.elf.databinding.ActivityGameBinding
import com.elf.real.elf.game.ButtonObject
import com.elf.real.elf.game.viewmodel.GameViewModel
import com.elf.real.elf.result.ResultActivity
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {
    private var isSinglePlayerKey = "is_single_player"
    private var gameScoreKey = "game_score"

    private lateinit var binding: ActivityGameBinding
    private lateinit var vmGame: GameViewModel

    private var boardList = mutableListOf<ButtonObject>()
    private var turn = "Turn X"
    private var isSinglePlayer: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isSinglePlayer = intent.getBooleanExtra(isSinglePlayerKey, false)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initBoard()
        vmGame = GameViewModel(boardList, isSinglePlayer)
        observe()
    }

    private fun initBoard() {
        boardList.add(ButtonObject(binding.a1))
        boardList.add(ButtonObject(binding.a2))
        boardList.add(ButtonObject(binding.a3))
        boardList.add(ButtonObject(binding.b1))
        boardList.add(ButtonObject(binding.b2))
        boardList.add(ButtonObject(binding.b3))
        boardList.add(ButtonObject(binding.c1))
        boardList.add(ButtonObject(binding.c2))
        boardList.add(ButtonObject(binding.c3))
    }

    fun boardTapped(view: View) {
        if (view !is ImageButton)
            return
        if (isSinglePlayer && turn == "Turn O") {
            return
        }
        for (button in boardList) {
            if (button.button == view) {
                vmGame.addToBoard(button)
            }

        }
    }

    private fun observe() {
        lifecycleScope.launch {
            vmGame.turn.collect {
                if (it != "") {
                    binding.turnTV.text = it
                    turn = it
                }
            }
        }

        lifecycleScope.launch {
            vmGame.result.collect {
                if (it != "") {
                    val nextActivity = Intent(this@GameActivity, ResultActivity::class.java)
                    nextActivity.putExtra(gameScoreKey, it)
                    nextActivity.putExtra(isSinglePlayerKey, isSinglePlayer)
                    startActivity(nextActivity)
                    finish()
                }
            }
        }
    }
}