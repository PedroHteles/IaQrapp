package com.example.camopencv;

public class StatusQr {
    String endereco;
    String produto;
    String status;

    public StatusQr(String endereco, String produto, String status) {
        this.endereco = endereco;
        this.produto = produto;
        this.status = status;
    }
    public StatusQr(){

    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
