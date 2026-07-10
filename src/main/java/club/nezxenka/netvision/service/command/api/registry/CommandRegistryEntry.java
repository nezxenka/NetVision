package club.nezxenka.netvision.service.command.api.registry;

import club.nezxenka.netvision.service.command.api.NetVisionCommand;

public record CommandRegistryEntry(String name, NetVisionCommand command, boolean playerOnly) {}
