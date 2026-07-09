package club.nezxenka.netvision.command.api.registry;

import club.nezxenka.netvision.command.api.NetVisionCommand;

public record CommandRegistryEntry(String name, NetVisionCommand command, boolean playerOnly) {}
