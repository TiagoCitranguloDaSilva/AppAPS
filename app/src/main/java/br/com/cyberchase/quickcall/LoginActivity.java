package br.com.cyberchase.quickcall;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.com.cyberchase.quickcall.data.repository.DemoDataSeeder;
import br.com.cyberchase.quickcall.data.service.LoginApiService;
import br.com.cyberchase.quickcall.model.Usuario;
import br.com.cyberchase.quickcall.network.HttpJsonClient;
import br.com.cyberchase.quickcall.network.NetworkPolicy;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText senhaInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkPolicy.enable();
        new DemoDataSeeder(this).seedIfNeeded();

        // O token do servidor expira depois de algumas horas.
        // Por isso, toda vez que o app abre, pedimos o login de novo.
        new SessionManager(this).logout();
        HttpJsonClient.setToken(null);

        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.input_email);
        senhaInput = findViewById(R.id.input_senha);
        Button entrarButton = findViewById(R.id.button_entrar);
        TextView credenciaisView = findViewById(R.id.text_credenciais_demo);

        credenciaisView.setText(
                "Teste: admin@quickcall.com / admin"
        );

        entrarButton.setOnClickListener(v -> realizarLogin());
    }

    private void realizarLogin() {
        String email = emailInput.getText().toString().trim();
        String senha = senhaInput.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha email e senha.", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario usuario = new LoginApiService().login(email, senha);

        if (usuario != null && usuario.getId() != null) {
            new SessionManager(this).salvarToken(HttpJsonClient.getToken());
            new SessionManager(this).salvarSessao(
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getTipoPerfil() == null ? null : usuario.getTipoPerfil().name()
            );
            abrirTelaPrincipal();
            return;
        }

        Toast.makeText(this, "Email/senha invalidos ou servidor desligado.", Toast.LENGTH_LONG).show();
    }

    private void abrirTelaPrincipal() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
