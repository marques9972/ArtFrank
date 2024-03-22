package com.example.artfrank.model

data class Movimentacao(
    val ano: Int = 0,
    val createdAt: Long? = null,
    val descricao: String = "",
    val despesa: String? = null,
    val dia: Int = 0,
    val documentos: List<String> = emptyList(),
    val mes: Int = 0,
    val recebimento: String? = null
)
