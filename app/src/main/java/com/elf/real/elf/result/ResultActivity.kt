package com.elf.real.elf.result

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.elf.real.elf.databinding.ActivityResultBinding
import com.elf.real.elf.game.ui.GameActivity
import com.elf.real.elf.menu.GameMenuActivity

class ResultActivity : AppCompatActivity() {
    private var isSinglePlayerKey = "is_single_player"
    private var gameScoreKey = "game_score"
    private lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val gameResult = intent.getStringExtra(gameScoreKey)
        val isSinglePlayer = intent.getBooleanExtra(isSinglePlayerKey, false)
        binding.resultTitle.text = gameResult
        binding.continueButton.setOnClickListener {
            val nextActivity = Intent(this, GameActivity::class.java)
            nextActivity.putExtra(isSinglePlayerKey, isSinglePlayer)
            startActivity(nextActivity)
        }
        binding.exitButton.setOnClickListener {
            val nextActivity = Intent(this, GameMenuActivity::class.java)
            startActivity(nextActivity)
        }
    }
}