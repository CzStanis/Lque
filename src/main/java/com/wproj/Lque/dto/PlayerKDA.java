package com.wproj.Lque.dto;

public class PlayerKDA {
        private final double kda;

        public PlayerKDA(int kills, int deaths, int assists) {
            this.kda = deaths > 0 ? (double) (kills + assists) / deaths : (double) (kills + assists);
        }

        public double getKda() {
            return kda;
        }

        @Override
        public String toString() {
            return String.format("%.2f", kda);
        }

}
