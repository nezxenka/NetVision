package club.nezxenka.netvision.remote.model;

import com.google.gson.annotations.SerializedName;

public record AIResponse(@SerializedName("probability") double probability) {}
