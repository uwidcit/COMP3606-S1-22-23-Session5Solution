import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import dev.kwasi.wk2.R

interface PasswordDialogListener {
    fun onPasswordEntered(password: String)
    fun onCancel()
}

class PasswordDialogFragment : DialogFragment() {
    private var listener: PasswordDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_password_dialog, null)

        val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)

        builder.setView(view)
            .setTitle("Enter Password")
            .setPositiveButton("OK") { dialog, _ ->
                val password = passwordEditText.text.toString()
                listener?.onPasswordEntered(password)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                listener?.onCancel()
                dialog.cancel()
            }

        return builder.create()
    }

    fun setListener(listener: PasswordDialogListener) {
        this.listener = listener
    }
}
