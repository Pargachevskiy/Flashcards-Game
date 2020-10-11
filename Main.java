package flashcards;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main {
    static String thing;
    static String definition;
    static Scanner scanner = new Scanner(System.in);
    static Map<String, Integer> mistakesStatistic = new LinkedHashMap<>();
    static Map<String, String> cards = new LinkedHashMap<>();
    public static void main(String[] args) {
        List<String> arguments = new ArrayList<>(List.of(args));
        if (arguments.contains("-import")) {
            int index = arguments.indexOf("-import") + 1;
            importCards(arguments.get(index));
        }
        menu();
        if (arguments.contains("-export")) {
            int index = arguments.indexOf("-export") + 1;
            exportCards(arguments.get(index));
        }
    }
    public static void menu() {
        boolean powerON= true;
        while (powerON) {
            Scanner scan = new Scanner(System.in);
            System.out.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            switch (scan.nextLine()) {
                case ("add") :
                    createCard();
                    break;
                case ("remove") : removeCard();
                    break;
                case ("import") :
                    System.out.println("File name:");
                    importCards(scan.nextLine());
                    break;
                case ("export") :
                    System.out.println("File name:");
                    exportCards(scan.nextLine());
                    break;
                case ("hardest card") :
                    hardestCard();
                    break;
                case ("reset stats") :
                    resetStatistics();
                    break;
                case ("log") :
                    statisticLog();
                    break;
                case ("ask") :
                    System.out.println("How many times to ask?");
                    int check = scan.nextInt();
                    System.out.println();
                    for (int i = 0; i < check; i++) {
                        askCard();
                    }
                    break;
                case ("exit") : powerON = false;
                    System.out.println("Bye bye!\n");
                    break;
            }
        }

    }
    public static void createCard() {
        System.out.println("The card:");
        thing = scanner.nextLine();
        if (cards.containsKey(thing)) {
            System.out.println("The card \"" + thing + "\" already exists.");
            return;
        }
        System.out.println("The definition of the card:");
        definition = scanner.nextLine();
        if (cards.containsValue(definition)) {
            System.out.println("The definition \"" + definition + "\" already exists.");
            return;
        }
        cards.put(thing, definition);
        mistakesStatistic.putIfAbsent(thing, 0);
        System.out.println("The pair(\"" + thing + "\" : \"" + definition + "\") has been added.\n");
    }
    public static void removeCard() {
        System.out.println("The card:");
        thing = scanner.nextLine();
        if (cards.containsKey(thing)) {
            cards.remove(thing);
            mistakesStatistic.remove(thing);
            System.out.println("The card has been removed");
        } else {
            System.out.println("Can't remove \"" + thing + "\": there is no such card.\n");
        }
    }
    public static void importCards(String fileName){
        File file = new File("./" + fileName);
        if (!file.exists()) {
            System.out.println("File not found.\n");
            return;
        }
        int count = 0;
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                thing = fileScanner.nextLine();
                definition = fileScanner.nextLine();
                int mistakes = Integer.parseInt(fileScanner.nextLine());
                mistakesStatistic.put(thing, mistakes);
                cards.put(thing, definition);
                count++;
            }
            System.out.println(count + " cards have been loaded.\n");
        } catch (IOException e) {
            System.out.println("No such file.\n");
        }
    }
    public static void exportCards(String fileName){
        File file = new File("./" + fileName);
        if(file.exists()) {
            file.delete();
        }
        int count = 0;
        try (PrintWriter print = new PrintWriter(file)) {
            for (String elem : cards.keySet()) {
                print.println(elem);
                print.println(cards.get(elem));
                print.println(mistakesStatistic.get(elem));
                mistakesStatistic.put(elem, 0);
                count++;
            }
            System.out.println(count + " cards have been saved\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void askCard() {
        List<String> list = new ArrayList<>();
        list.addAll(cards.keySet());
        Scanner scan = new Scanner(System.in);
        Random random = new Random();
        thing = list.get(random.nextInt(list.size()));
        String rightFor = "";
        System.out.println("Print the definition of \"" + thing + "\":");
        definition = scan.nextLine();
        if (definition.equals(cards.get(thing))) {
            System.out.println("Correct!\n");
        } else {
            int errCounter = mistakesStatistic.get(thing) + 1;
            mistakesStatistic.put(thing, errCounter);
            if (cards.containsValue(definition)) {
                for (var enter : cards.entrySet()) {
                    if (definition.equals(enter.getValue())) {
                        rightFor = enter.getKey();
                    }
                }
                System.out.println("Wrong. The right answer is \"" + cards.get(thing) + "\", but your definition is correct for \"" + rightFor + "\".\n");
            } else {
                System.out.println("Wrong. The right answer is \"" + cards.get(thing) + "\".\n");
            }
        }
    }
    public static void hardestCard() {
        int errorsCounter = 0;
        Set<String> things = new HashSet<>();
        for (String elem : mistakesStatistic.keySet()) {
            int currentCount = mistakesStatistic.get(elem);
            if (currentCount > errorsCounter) {
                things.clear();
                things.add(elem);
                errorsCounter = currentCount;
            } else if (currentCount == errorsCounter && currentCount != 0) {
                things.add(elem);
            }
        }
        if (errorsCounter == 0) {
            System.out.println("There are no cards with errors.");
        } else {
            String output ="";
            boolean check = true;
            for (String elem : things) {
                if (check) {
                    output += "\"" + elem + "\"";
                    check = false;
                } else {
                    output += ", \"" + elem + "\"";
                }
            }
            if (things.size() == 1) {
                output = "The hardest card is " + output + ". You have " + errorsCounter + " errors answering it";
            } else {
                output = "The hardest cards are " + output + ". You have " + errorsCounter + " errors answering them";
            }
            System.out.println(output);
        }
    }
    public static void resetStatistics() {
        mistakesStatistic.replaceAll((e, v) -> 0);
        System.out.println("Card statistics have been reset.");
    }
    public static  void statisticLog () {
        Scanner scan = new Scanner(System.in);
        System.out.println("File name:");
        try (PrintWriter print = new PrintWriter(new File("./" + scan.nextLine()))) {
            for (String elem : mistakesStatistic.keySet()) {
                print.println(elem + " : " + mistakesStatistic.get(elem));
            }
            System.out.println("The log has been saved.\n");
        } catch (IOException e) {
            System.out.println("No such file\n");
        }

    }
}
