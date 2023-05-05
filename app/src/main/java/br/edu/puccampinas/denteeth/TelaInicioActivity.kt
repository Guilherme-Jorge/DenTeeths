package br.edu.puccampinas.denteeth

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import br.edu.puccampinas.denteeth.databinding.ActivityTelaInicioBinding
import br.edu.puccampinas.denteeth.emergencia.ListaEmergenciaActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class TelaInicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTelaInicioBinding
    private lateinit var auth: FirebaseAuth

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            val intentDisabledNotification = Intent(this, DisabledNotificationActivity::class.java)

            this.startActivity(intentDisabledNotification)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askNotificationPermission()

        binding.btnEntrar.setOnClickListener {
            hideSoftKeyboard(binding.btnEntrar)

            if (binding.etEmail.text?.isEmpty() == true) {
                Snackbar.make(binding.root, "Campo de E-mail vazio.", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (binding.etSenha.text?.isEmpty() == true) {
                Snackbar.make(binding.root, "Campo de Senha vazio.", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            newLogin(binding.etEmail.text.toString(), binding.etSenha.text.toString())
        }

        binding.btnRegistrar.setOnClickListener {
            hideSoftKeyboard(binding.btnRegistrar)

            //Inicializaçâo do Intent para a tela de Registro
            val intentCriarConta = Intent(this, CriarContaActivity::class.java)

            this.startActivity(intentCriarConta)
        }
    }

    private fun hideSoftKeyboard(v: View) {

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // TODO: Colocar permissao para API < 33
        }
    }

    private fun newLogin(email: String, password: String) {
        hideSoftKeyboard(binding.btnEntrar)

        auth = Firebase.auth

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
//                    val intentListaEmergencia = Intent(this, ListaEmergenciaActivity::class.java)
//
//                    this.startActivity(intentListaEmergencia)
                    Snackbar.make(
                        binding.root,
                        "Login realizado com sucesso!",
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    if (it.exception is FirebaseAuthException) {
                        Snackbar.make(
                            binding.root,
                            "Não foi possível fazer o login, verifique os dados e tente novamente.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }
}