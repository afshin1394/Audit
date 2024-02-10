import android.R
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager

class LoadingDialog(val activity: Activity) :
    AlertDialog(activity, R.style.Theme_Translucent_NoTitleBar_Fullscreen) {

     private var alertDialog : AlertDialog

    init {
      val dialogView = layoutInflater.inflate(com.irancell.nwg.ios.R.layout.fragment_loading, window?.decorView?.rootView as ViewGroup,false)

      val alertDialogBuilder = Builder(activity)
          .setView(dialogView) // Set the custom view here

      alertDialogBuilder.setCancelable(false);
      val  dialog = alertDialogBuilder.show();

      dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);


      alertDialog = alertDialogBuilder.create()
  }


     fun showLoading() {
        // Create a custom view for the dialog
        alertDialog.show()
    }
   fun dismissLoading(){
       alertDialog.dismiss()
   }

}