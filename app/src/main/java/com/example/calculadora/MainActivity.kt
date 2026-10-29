package com.example.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculadoraApp()
                }
            }
        }
    }
}

@Composable
fun CalculadoraApp() {
    // CONCEITOS: mutableStateOf e rememberSaveable
    // rememberSaveable mantém os dados salvos mesmo se o celular girar (mudar de orientação)
    var visor by rememberSaveable { mutableStateOf("0") }
    var primeiroNumero by rememberSaveable { mutableStateOf("") }
    var operadorAtual by rememberSaveable { mutableStateOf("") }
    var aguardandoNovoNumero by rememberSaveable { mutableStateOf(false) }

    // FUNÇÃO: Lógica de limpar tudo (C)
    fun limpar() {
        visor = "0"
        primeiroNumero = ""
        operadorAtual = ""
        aguardandoNovoNumero = false
    }

    // FUNÇÃO: Lógica do botão apagar (⌫)
    fun apagar() {
        if (visor != "0" && visor != "Erro") {
            visor = if (visor.length > 1) visor.dropLast(1) else "0"
        }
    }

    // FUNÇÃO: Lógica de clicar nos números e no ponto decimal
    fun digitarNumero(numero: String) {
        if (visor == "Erro") limpar() // Se der erro, começa de novo

        if (aguardandoNovoNumero) {
            visor = if (numero == ".") "0." else numero
            aguardandoNovoNumero = false
        } else {
            // Evitar múltiplos pontos decimais (ex: 1.2.3)
            if (numero == "." && visor.contains(".")) return

            // Substituir o "0" inicial
            if (visor == "0" && numero != ".") {
                visor = numero
            } else {
                visor += numero
            }
        }
    }

    // FUNÇÃO: Lógica matemática de cálculo
    fun calcular() {
        if (primeiroNumero.isNotEmpty() && operadorAtual.isNotEmpty() && visor != "Erro") {
            val num1 = primeiroNumero.toDoubleOrNull() ?: 0.0
            val num2 = visor.toDoubleOrNull() ?: 0.0
            var resultado = 0.0

            // OPERADORES MATEMÁTICOS
            when (operadorAtual) {
                "+" -> resultado = num1 + num2
                "-" -> resultado = num1 - num2
                "*" -> resultado = num1 * num2
                "/" -> {
                    // TRATAMENTO DE ERRO: Divisão por zero
                    if (num2 == 0.0) {
                        visor = "Erro"
                        primeiroNumero = ""
                        operadorAtual = ""
                        aguardandoNovoNumero = true
                        return
                    }
                    resultado = num1 / num2
                }
            }

            // Formatar o resultado para não mostrar ".0" se for inteiro
            visor = if (resultado % 1.0 == 0.0) {
                resultado.toInt().toString()
            } else {
                resultado.toString()
            }

            primeiroNumero = ""
            operadorAtual = ""
            aguardandoNovoNumero = true
        }
    }

    // FUNÇÃO: Lógica ao clicar em um operador (+, -, *, /)
    fun escolherOperador(op: String) {
        if (visor == "Erro") return

        if (primeiroNumero.isNotEmpty() && operadorAtual.isNotEmpty() && !aguardandoNovoNumero) {
            calcular() // Calcula o que estava pendente antes de colocar o novo operador
        }

        primeiroNumero = visor
        operadorAtual = op
        aguardandoNovoNumero = true
    }

    // INTERFACE VISUAL (UI)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        // CONCEITO: Text (Visor)
        Text(
            text = visor,
            fontSize = 64.sp,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )

        // Linha 1: C, Apagar, Divisão, Multiplicação
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            BotaoCalculadora("C", Modifier.weight(1f)) { limpar() }
            BotaoCalculadora("⌫", Modifier.weight(1f)) { apagar() }
            BotaoCalculadora("/", Modifier.weight(1f)) { escolherOperador("/") }
            BotaoCalculadora("*", Modifier.weight(1f)) { escolherOperador("*") }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Linha 2: 7, 8, 9, Subtração
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            BotaoCalculadora("7", Modifier.weight(1f)) { digitarNumero("7") }
            BotaoCalculadora("8", Modifier.weight(1f)) { digitarNumero("8") }
            BotaoCalculadora("9", Modifier.weight(1f)) { digitarNumero("9") }
            BotaoCalculadora("-", Modifier.weight(1f)) { escolherOperador("-") }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Linha 3: 4, 5, 6, Adição
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            BotaoCalculadora("4", Modifier.weight(1f)) { digitarNumero("4") }
            BotaoCalculadora("5", Modifier.weight(1f)) { digitarNumero("5") }
            BotaoCalculadora("6", Modifier.weight(1f)) { digitarNumero("6") }
            BotaoCalculadora("+", Modifier.weight(1f)) { escolherOperador("+") }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Linha 4: 1, 2, 3, Igual
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            BotaoCalculadora("1", Modifier.weight(1f)) { digitarNumero("1") }
            BotaoCalculadora("2", Modifier.weight(1f)) { digitarNumero("2") }
            BotaoCalculadora("3", Modifier.weight(1f)) { digitarNumero("3") }
            BotaoCalculadora("=", Modifier.weight(1f)) { calcular() }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Linha 5: 0, Ponto Decimal
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            BotaoCalculadora("0", Modifier.weight(1f)) { digitarNumero("0") }
            BotaoCalculadora(".", Modifier.weight(1f)) { digitarNumero(".") }
            // Botão vazio para alinhamento visual, ou podemos fazer o "0" ocupar mais espaço.
            Spacer(modifier = Modifier.weight(2f))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// CONCEITO: Button - Criando um componente reutilizável para os botões
@Composable
fun BotaoCalculadora(texto: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(1.2f), // Deixa os botões em formato padronizado
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text = texto, fontSize = 24.sp)
    }
}