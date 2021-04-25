package mm.com.myinstantappdemo.tools

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import mm.com.myinstantappdemo.R

fun Context.showExitAlertDialog(
    positiveButton: (AlertDialog.Builder) -> Unit,
) {
    val dialog = AlertDialog.Builder(this)
    dialog.setMessage(getString(R.string.are_you_sure_you_want_to_exit))
        .setCancelable(false)
        .setPositiveButton(getString(R.string.yes)) { _, _ -> positiveButton(dialog) }
        .setNegativeButton(getString(R.string.no)) { dialog2, _ -> dialog2.dismiss() }
        .show()
}


// shows this view
fun View.show(): View {
    this.visibility = View.VISIBLE
    return this
}

// makes the view gone
fun View.gone(): View {
    this.visibility = View.GONE
    return this
}
