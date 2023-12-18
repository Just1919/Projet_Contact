package com.example.contactpro;

import java.io.Serializable;

public class Contact implements Serializable {
        private  String nom;
        private  String prenom;
        private  String tel;
        private String email;
        private String service;
        private String url;
        private  boolean favori;

        public Contact(String nom, String prenom, String tel, String email, String service, String url,boolean favori) {
            this.nom = nom;
            this.prenom = prenom;
            this.tel = tel;
            this.email = email;
            this.service = service;
            this.url = url;
            this.favori=favori;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isFavori() {
            return favori;
        }

        public void setFavori(boolean favori) {
            this.favori = favori;
        }
}

