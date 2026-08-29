package br.com.controledoacoes.model;

public class Item {

    private int id;
    private String nome;
    private String categoria;
    private int quantidadeAtual;

    public Item() {
    }

    public Item(int id, String nome, String categoria, int quantidadeAtual) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.quantidadeAtual = quantidadeAtual;
    }

    public Item(String nome, String categoria) {
        this.nome = nome;
        this.categoria = categoria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(int quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }

    @Override
    public String toString() {
        return nome;
    }
}
