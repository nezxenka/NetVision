package club.nezxenka.netvision.server.model;

import com.google.gson.annotations.SerializedName;

public record AIResponse(@SerializedName("probability") double probability) {}
