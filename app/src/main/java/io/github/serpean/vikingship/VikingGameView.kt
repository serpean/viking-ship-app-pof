package io.github.serpean.vikingship

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import io.github.serpean.vikingship.canvas.Rock
import io.github.serpean.vikingship.canvas.Wind


class VikingGameView : View {

    private val windPlayers: MutableMap<Int, Wind> = mutableMapOf()
    private var game: Game? = null

    // Texture create here to avoid resources resolution issues
    private val vikingShip = BitmapFactory.decodeResource(resources, R.drawable.viking_ship)
    private val island = BitmapFactory.decodeResource(resources, R.drawable.island)
    private val rocks: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.rocks)
    private val wind = BitmapFactory.decodeResource(resources, R.drawable.wind)

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onDraw(canvas: Canvas?) {
        if (game == null) {
            game = Game(this.measuredWidth, this.measuredHeight)
        }
        if (game!!.isWinner()) {
            Toast.makeText(context, "Winner!", Toast.LENGTH_SHORT).show()
            game = null
            return
        }
        if (game!!.isLooser()) {
            Toast.makeText(context, "Looser!", Toast.LENGTH_SHORT).show()
            game = null
            return
        }
        if (game != null) {
            for (rock: Rock in game!!.rocks) {
                rock.draw(canvas, rocks)
            }
            game!!.island.draw(canvas, island)
            game!!.ship.draw(canvas, vikingShip)
        }
        for (path: Wind in windPlayers.values) {
            for (windPlayer: Wind in windPlayers.values) {
                windPlayer.draw(canvas, wind)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val index = event.actionIndex
        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_DOWN -> {
                val newWindPlayer = Wind(event.getX(index), event.getY(index));
                Log.i("down id", event.getPointerId(index).toString());
                windPlayers[event.getPointerId(index)] = newWindPlayer;
            }
            MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_UP -> {
                val currentWindPlayerId = event.getPointerId(index)
                Log.i("up id", currentWindPlayerId.toString());
                windPlayers.remove(currentWindPlayerId);
            }
            MotionEvent.ACTION_MOVE -> {
                for (i: Int in windPlayers.keys) {
                    val pointerIndex: Int = event.findPointerIndex(i);
                    val currentX = event.getX(pointerIndex)
                    val currentY = event.getY(pointerIndex)
                    windPlayers[i] = Wind(currentX, currentY)
                    val speed = if (windPlayers.size > 3) ((100..200).random()/100).toFloat() else 0F
                    game?.moveShip(currentX, currentY, speed)
                }
            }
        }
        invalidate()
        return true
    }
}