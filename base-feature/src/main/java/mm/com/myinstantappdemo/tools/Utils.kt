package mm.com.myinstantappdemo.tools

import android.content.Context
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