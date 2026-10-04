package br.com.cyberchase.quickcall.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpJsonClient {

    // Token (o "cracha") recebido no login. E enviado em todos os pedidos.
    private static String token;

    public static void setToken(String novoToken) {
        token = novoToken;
    }

    public static String getToken() {
        return token;
    }

    // Mensagem do ultimo erro que o servidor devolveu (ex.: "O id do tecnico e obrigatorio").
    private static String ultimoErro;

    public static String getUltimoErro() {
        return ultimoErro;
    }

    public JSONArray getArray(String path) {
        try {
            String response = request("GET", path, null);
            return response == null || response.isBlank() ? new JSONArray() : new JSONArray(response);
        } catch (IOException | JSONException e) {
            return new JSONArray();
        }
    }

    public JSONObject getObject(String path) {
        try {
            String response = request("GET", path, null);
            return response == null || response.isBlank() ? null : new JSONObject(response);
        } catch (IOException | JSONException e) {
            return null;
        }
    }

    public JSONObject post(String path, JSONObject body) {
        try {
            String response = request("POST", path, body);
            return response == null || response.isBlank() ? null : new JSONObject(response);
        } catch (IOException | JSONException e) {
            return null;
        }
    }

    public JSONObject put(String path, JSONObject body) {
        try {
            String response = request("PUT", path, body);
            return response == null || response.isBlank() ? null : new JSONObject(response);
        } catch (IOException | JSONException e) {
            return null;
        }
    }

    public JSONObject delete(String path) {
        try {
            String response = request("DELETE", path, null);
            return response == null || response.isBlank() ? null : new JSONObject(response);
        } catch (IOException | JSONException e) {
            return null;
        }
    }

    public boolean deleteResource(String path) {
        try {
            request("DELETE", path, null);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private String request(String method, String path, JSONObject body) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(ApiConfig.BASE_URL + path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            if (token != null && !token.isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoInput(true);

            if (body != null) {
                connection.setDoOutput(true);
                writeBody(connection, body.toString());
            }

            ultimoErro = null;
            int responseCode = connection.getResponseCode();
            boolean sucesso = responseCode >= 200 && responseCode < 300;
            InputStream stream = sucesso ? connection.getInputStream() : connection.getErrorStream();
            String corpo = stream == null ? null : readBody(stream);

            // Se o servidor respondeu com erro (400, 403, 500...), NAO tratamos como sucesso.
            // Guardamos a mensagem do servidor para mostrar na tela.
            if (!sucesso) {
                ultimoErro = "Erro " + responseCode;
                try {
                    JSONObject erro = new JSONObject(corpo);
                    if (erro.has("mensagem")) {
                        ultimoErro = erro.getString("mensagem");
                    }
                } catch (Exception ignored) {
                    // corpo do erro nao era JSON
                }
                throw new IOException(ultimoErro);
            }

            return corpo;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void writeBody(HttpURLConnection connection, String body) throws IOException {
        OutputStream outputStream = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        writer.write(body);
        writer.flush();
        writer.close();
        outputStream.close();
    }

    private String readBody(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        inputStream.close();
        return builder.toString();
    }
}
