package org.kerix.karaapi.api.command.argument;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.kerix.karaapi.api.command.CommandContext;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public final class Arguments {

    private Arguments() {
    }

    public static ArgumentType<String> string() {
        return (context, input) -> input;
    }

    public static ArgumentType<Integer> integer() {
        return (context, input) -> {
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException exception) {
                throw new ArgumentParseException("'" + input + "' is not an integer.");
            }
        };
    }

    public static ArgumentType<Long> longNumber() {
        return (context, input) -> {
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException exception) {
                throw new ArgumentParseException("'" + input + "' is not a long number.");
            }
        };
    }

    public static ArgumentType<Double> decimal() {
        return (context, input) -> {
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException exception) {
                throw new ArgumentParseException("'" + input + "' is not a decimal number.");
            }
        };
    }

    public static ArgumentType<Boolean> bool() {
        return (context, input) -> switch (input.toLowerCase(Locale.ROOT)) {
            case "true", "yes", "y", "1", "on" -> true;
            case "false", "no", "n", "0", "off" -> false;
            default -> throw new ArgumentParseException("'" + input + "' is not true or false.");
        };
    }

    public static ArgumentType<Player> player() {
        return new ArgumentType<>() {
            @Override
            public Player parse(CommandContext context, String input) {
                Player player = Bukkit.getPlayerExact(input);

                if (player == null) {
                    throw new ArgumentParseException("Player not found: " + input);
                }

                return player;
            }

            @Override
            public List<String> suggest(CommandContext context, String input) {
                return Bukkit.getOnlinePlayers()
                        .stream()
                        .map(Player::getName)
                        .filter(name -> startsWith(name, input))
                        .toList();
            }
        };
    }

    public static ArgumentType<OfflinePlayer> offlinePlayer() {
        return new ArgumentType<>() {
            @Override
            public OfflinePlayer parse(CommandContext context, String input) {
                return Bukkit.getOfflinePlayer(input);
            }

            @Override
            public List<String> suggest(CommandContext context, String input) {
                return Bukkit.getOnlinePlayers()
                        .stream()
                        .map(Player::getName)
                        .filter(name -> startsWith(name, input))
                        .toList();
            }
        };
    }

    public static ArgumentType<World> world() {
        return new ArgumentType<>() {
            @Override
            public World parse(CommandContext context, String input) {
                World world = Bukkit.getWorld(input);

                if (world == null) {
                    throw new ArgumentParseException("World not found: " + input);
                }

                return world;
            }

            @Override
            public List<String> suggest(CommandContext context, String input) {
                return Bukkit.getWorlds()
                        .stream()
                        .map(World::getName)
                        .filter(name -> startsWith(name, input))
                        .toList();
            }
        };
    }

    public static ArgumentType<Material> material() {
        return new ArgumentType<>() {
            @Override
            public Material parse(CommandContext context, String input) {
                Material material = Material.matchMaterial(input);

                if (material == null) {
                    throw new ArgumentParseException("Material not found: " + input);
                }

                return material;
            }

            @Override
            public List<String> suggest(CommandContext context, String input) {
                String lower = lower(input);

                return Arrays.stream(Material.values())
                        .filter(Material::isItem)
                        .map(material -> material.name().toLowerCase(Locale.ROOT))
                        .filter(name -> name.startsWith(lower))
                        .limit(50)
                        .toList();
            }
        };
    }

    public static <E extends Enum<E>> ArgumentType<E> enumType(Class<E> enumClass) {
        return new ArgumentType<>() {
            @Override
            public E parse(CommandContext context, String input) {
                try {
                    return Enum.valueOf(enumClass, input.toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException exception) {
                    throw new ArgumentParseException("Invalid value: " + input);
                }
            }

            @Override
            public List<String> suggest(CommandContext context, String input) {
                String lower = lower(input);

                return Arrays.stream(enumClass.getEnumConstants())
                        .map(value -> value.name().toLowerCase(Locale.ROOT))
                        .filter(name -> name.startsWith(lower))
                        .toList();
            }
        };
    }

    public static ArgumentType<String> choice(String... choices) {
        List<String> allowed = Arrays.stream(choices == null ? new String[0] : choices)
                .filter(choice -> choice != null && !choice.isBlank())
                .map(choice -> choice.toLowerCase(Locale.ROOT))
                .toList();

        return mappedChoice(Function.identity(), allowed);
    }

    public static <T> ArgumentType<T> mappedChoice(Function<String, T> mapper, List<String> choices) {
        return new ArgumentType<>() {
            @Override
            public T parse(CommandContext context, String input) {
                String lower = lower(input);

                if (!choices.contains(lower)) {
                    throw new ArgumentParseException("Invalid value: " + input);
                }

                return mapper.apply(lower);
            }

            @Override
            public List<String> suggest(CommandContext context, String input) {
                String lower = lower(input);

                return choices.stream()
                        .filter(choice -> choice.startsWith(lower))
                        .toList();
            }
        };
    }

    private static boolean startsWith(String value, String input) {
        return value.toLowerCase(Locale.ROOT).startsWith(lower(input));
    }

    private static String lower(String input) {
        return input == null ? "" : input.toLowerCase(Locale.ROOT);
    }
}
