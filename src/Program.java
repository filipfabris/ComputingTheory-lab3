import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


class Key {

    public final String stanje;
    public final String ulaz;
    public final String stog;


    Key(String stanje, String ulaz, String stog) {
        this.stanje = stanje;
        this.ulaz = ulaz;
        this.stog = stog;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Key)) return false;
        Key key = (Key) o;
        return stanje.equals(key.stanje) && ulaz.equals(key.ulaz) && stog.equals(key.stog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stanje, ulaz, stog);
    }

    @Override
    public String toString() {
        return "{" + "stanje='" + stanje + '\'' + ", ulaz='" + ulaz + '\'' +  ", stog='" + stog + '\'' + '}';
    }
}


class Value {

    public final String stanje;
    public final String[] stog;


    Value(String stanje, String[] stog) {
        this.stanje = stanje;
        this.stog = stog;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Value)) return false;
        Value value = (Value) o;
        return Objects.equals(stanje, value.stanje) && Arrays.equals(stog, value.stog);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(stanje);
        result = 31 * result + Arrays.hashCode(stog);
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "stanje='" + stanje + '\'' +
                ", stog=" + Arrays.toString(stog) +
                '}';
    }
}

public class Program {

    List<List<String>> inputs;
    List<String> states;
    List<String> abeceda;
    List<String> stog;
    List<String> prihvatljivaStanja;
    String PocetnoStanje;
    String PocetnoStanjeStoga;
    HashMap<Key, Value> transitions;
    Boolean recurisveSet = false;
    Boolean recursiveFail = false;

    String passTrenutnoStanjeByReference = "";
    String passTrenutnoStogByReference = "";

    //q1#K|q1#NK|0
    //q1,0,K->q1,NK

    String PotisniAutomat(List<String> ulazi, LinkedList<String> recursiveStack, String recursiveTrenutnoStanje){
        LinkedList<String> stack = new LinkedList<>();
        ListIterator<String> listIterator;
        Boolean break_zastavica = false;
        String output = "";

        String trenutnoStanje = PocetnoStanje;
        String trenutnoStog = PocetnoStanjeStoga;

        stack.push(trenutnoStog);

        if(recursiveTrenutnoStanje != null){
            stack = recursiveStack;
            trenutnoStanje = recursiveTrenutnoStanje;
            recurisveSet = true;
        }else{
            output = PocetnoStanje + "#K|";
        }

        for(String input : ulazi){

            if(recursiveFail == true){
                break;
            }

            if(stack.size() >= 1) {
                trenutnoStog = stack.pop();
            } else {
                trenutnoStog = "$";
            }
            Key key = new Key(trenutnoStanje, input, trenutnoStog);

            Value value = transitions.get(key);

            if(value == null){
                String temp = ProvjeriPrazni(trenutnoStanje, input, trenutnoStog, stack);
                if(temp.length() >= 1){
                    output = output + temp;
                    trenutnoStanje = passTrenutnoStanjeByReference;
                    trenutnoStog = passTrenutnoStogByReference;
                    continue;
                }
                output = output + "fail|";
                break_zastavica = true;
                recursiveFail = true;
                break;
            }

            trenutnoStanje = value.stanje;
            output = output + value.stanje + "#";

            if(recursiveTrenutnoStanje != null){
                recursiveTrenutnoStanje = trenutnoStanje;
                passTrenutnoStogByReference = trenutnoStog;
                passTrenutnoStanjeByReference = trenutnoStanje;
            }

            if(!value.stog[0].equals("$")){ //Ako prazni znak nije za novi unos na stog
                for(int i = value.stog.length - 1; i>= 0; i--){ //Stavljanje novih znakova na stog
                    stack.push(value.stog[i]);
                }
            }

            listIterator = stack.listIterator();
            while (listIterator.hasNext()) //Korak za ispis, stanje stoga
            {
                output = output + listIterator.next();
            }

            output = output + "|";

        }

        if(recursiveTrenutnoStanje != null){
            return output;
        }

        while(true){

            if(break_zastavica == true){
                break;
            }

            if(recurisveSet == true){
                break;
            }

            if(prihvatljivaStanja.contains(trenutnoStanje)){
                break;
            }

            String input = "$";
            if(stack.size() >= 1) {
                trenutnoStog = stack.pop();
            } else {
                trenutnoStog = "$";
            }

            Key key = new Key(trenutnoStanje, input, trenutnoStog);

            Value value = transitions.get(key);

            if(value == null){
                break;
            }

            trenutnoStanje = value.stanje;


            output = output + value.stanje + "#";


                for(int i = value.stog.length - 1; i>= 0; i--){ //Stavljanje novih znakova na stog
                    stack.push(value.stog[i]);
                }


            if(stack.size() == 0){
                output = output + "$";
            }

            listIterator = stack.listIterator();
            while (listIterator.hasNext()) //Korak za ispis, stanje stoga
            {
                output = output + listIterator.next();
            }

            output = output + "|";


        }

        if(prihvatljivaStanja.contains(trenutnoStanje) && break_zastavica==false){ //Korak za ispis, da li je prihvatljivo stanje
            output = output + "1";
        } else{
            output = output + "0";
        }

        return output;
    }


    public String ProvjeriPrazni(String trenutnoStanje, String input, String trenutnoStog,  LinkedList<String> stack){
        //trenutnoStog
        //trenutnoStanje
        Key key = new Key(trenutnoStanje, "$", trenutnoStog);
        Value value = transitions.get(key);
        String output = "";
        ListIterator<String> listIterator;

        if(value == null){
            return "";
        }else{
            trenutnoStanje = value.stanje;
            output = output + value.stanje + "#";

                for(int i = value.stog.length - 1; i>= 0; i--){ //Stavljanje novih znakova na stog
                    stack.push(value.stog[i]);
                }


            listIterator = stack.listIterator();
            while (listIterator.hasNext()) //Korak za ispis, stanje stoga
            {
                output = output + listIterator.next();
            }

            output = output + "|";
            LinkedList<String> rekurzivniInput = new LinkedList<>();
            rekurzivniInput.push(input);
            output = output + PotisniAutomat(rekurzivniInput, stack, trenutnoStanje);
        }

        return output;
    }


    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        FileWriter writer = new FileWriter("./output.txt");
        Program automat = new Program();

        String s = reader.readLine();
        String str[] = s.split("\\|");


        automat.inputs = new LinkedList<List<String>>(); //Ulazi
        int i = 0;
        for (String element : str) {
            automat.inputs.add(new LinkedList<String>());
            String[] temp = element.split(",");

            for (String el : temp) {
                el.strip();
                automat.inputs.get(i).add(el);
            }
            i++;
        }

        s = reader.readLine();
        str = s.split(",");
        automat.states = Arrays.asList(str);   //Stanja

        s = reader.readLine();
        str = s.split(",");
        automat.abeceda = Arrays.asList(str);   //Abeceda

        s = reader.readLine();
        str = s.split(",");
        automat.stog = Arrays.asList(str);   //Stog abeceda

        s = reader.readLine();
        str = s.split(",");
        automat.prihvatljivaStanja = Arrays.asList(str);   //PrihvatljivaStanja

        s = reader.readLine();
        automat.PocetnoStanje = s;

        s = reader.readLine();
        automat.PocetnoStanjeStoga = s;


        automat.transitions = new HashMap<>();

        s = reader.readLine();
        while (s != null) {
            str = s.split("->");

            String[] keyTemp = str[0].split(",");
            Key key = new Key(keyTemp[0], keyTemp[1], keyTemp[2]);

//            System.out.println(key.toString());

            String[] valueTemp = str[1].split(",");
            String stackTemp[] = valueTemp[1].split("");
            Value value = new Value(valueTemp[0], stackTemp);


            automat.transitions.put(key, value);
            s = reader.readLine();
        }

        String izlaz = "";
        for(List<String> input_check: automat.inputs){
            s = automat.PotisniAutomat(input_check, null, null);
            izlaz = izlaz + s + "\n";
            writer.write(s + "\n");
            automat.passTrenutnoStanjeByReference = "";
            automat.passTrenutnoStogByReference = "";
            automat.recurisveSet = false;
            automat.recursiveFail = false;
        }

        System.out.print(izlaz);
    writer.close();
    }
}
