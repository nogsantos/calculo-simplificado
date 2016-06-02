package br.eti.fabricionogueira.mcalc;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText valor;
    private EditText valorPorcentagem;
    private TextView resPorcentagem;
    private TextView resAcrescimo;
    private TextView resDesconto;
    private TextView titPorcentagem;
    private TextView titAcrescimo;
    private TextView titDesconto;
    private Double valorCalc = 0d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * Somente português br
         */
        if(!Locale.getDefault().getLanguage().equals("pt") && !Locale.getDefault().getCountry().equals("BR")){
            new AlertDialog.Builder(this)
                .setTitle("Desculpe")
                .setMessage("O Aplicativo só está apto a operar em dispositivos com linguagem Português do Brasil.")
                .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .setCancelable(false)
                .show();
        }
        /**
         *
         */
        valor = (EditText) findViewById(R.id.txtValor);
        valorPorcentagem = (EditText) findViewById(R.id.txtPorcentagem);
        resPorcentagem = (TextView) findViewById(R.id.txtResPorcentagem);
        resAcrescimo = (TextView) findViewById(R.id.txtResAcrescimo);
        resDesconto = (TextView) findViewById(R.id.txtResDesconto);
        titPorcentagem = (TextView) findViewById(R.id.txtTitPorcentagem);
        titAcrescimo = (TextView) findViewById(R.id.txtTitAcrescimo);
        titDesconto = (TextView) findViewById(R.id.txtTitDesconto);
        /**
         * Focus no campo e habilita o teclado para digitação
         */
        valorPorcentagem.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        /**
         * Alteração na porcentagem
         */
        valorPorcentagem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                porcentagem();
                acrescimo();
                desconto();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        /**
         * Alteração do valor
         */
        valor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0) {
                    valorCalc = Double.parseDouble(s.toString().replaceAll("\\D+", "")) / 100;
                }
                porcentagem();
                acrescimo();
                desconto();
            }
            @Override
            public void afterTextChanged(Editable s) {
                formatarValor(s, this, valor);
            }
        });
    }
    /**
     * Calcula o valor correspondente a porcentagem informada
     */
    private void porcentagem(){
        Double res;
        Double percent = valorPorcentagem.getText().toString().length() > 0 ? Double.parseDouble(valorPorcentagem.getText().toString()) : 0;
        res = valorCalc * (percent/100);
        mostraResultado(titPorcentagem, resPorcentagem, res, 'p');
    }
    /**
     * Calcula o valor mais a porcentagem informada (Fator de multiplicação)
     */
    private void acrescimo(){
        Double res;
        Double percent = valorPorcentagem.getText().toString().length() > 0 ? Double.parseDouble(valorPorcentagem.getText().toString()) : 0;
        res = valorCalc * (1 + (percent / 100));
        mostraResultado(titAcrescimo, resAcrescimo, res, 'a');
    }
    /**
     * Calula o valor com desconto
     */
    private void desconto(){
        Double res;
        Double percent = valorPorcentagem.getText().toString().length() > 0 ? Double.parseDouble(valorPorcentagem.getText().toString()) : 0;
        res = valorCalc * ((100 - percent) / 100);
        mostraResultado(titDesconto, resDesconto, res, 'd');
    }
    /**
     * Mostra os resultados para o usuário
     */
    private void mostraResultado(TextView txtTitulo, TextView txtResultado, Double resultado, char tipo){
        if(valor.getText().length() > 0 && valorPorcentagem.getText().length() > 0){
            if(!valorCalc.equals("")){
                switch (tipo){
                    case 'p':
                        txtTitulo.setText(valorPorcentagem.getText()+"% de "+formatarValor(valor.getText()));
                        break;
                    case 'a':
                        txtTitulo.setText(formatarValor(valor.getText())+" + "+valorPorcentagem.getText()+"%");
                        break;
                    case 'd':
                        txtTitulo.setText(formatarValor(valor.getText())+" - "+valorPorcentagem.getText()+"%");
                        break;
                }
                txtResultado.setText(customFormat("R$ ###,###.###", resultado));
            }else{
                txtResultado.setText("0,0");
            }
        }
    }
    /**
     * Formatação de valores monetário no momento da digitação.
     * Ps.: Apenas pt-Br
     */
    private void formatarValor(Editable s, TextWatcher t, EditText e){
        if(!s.toString().equals("")){
            e.removeTextChangedListener(t);
            String replaceable = String.format("[R$,.]", NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
            String cleanString = s.toString().replaceAll(replaceable, "");
            double parsed = Double.parseDouble(cleanString);
            String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));
            e.setText(formatted);
            e.setSelection(formatted.length());
            e.addTextChangedListener(t);
        }
    }
    private String formatarValor(Editable s){
        if(!s.toString().equals("")){
            String replaceable = String.format("[R$,.]", NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
            String cleanString = s.toString().replaceAll(replaceable, "");
            double parsed = Double.parseDouble(cleanString);
            String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));
            return formatted;
        }
        return null;
    }
    /**
     * Formatação da saida
     */
    private String customFormat(String pattern, Double value ) {
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        String output = myFormatter.format(value);
        return output;
    }
}
