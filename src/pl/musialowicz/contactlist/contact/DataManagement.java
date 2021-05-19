package pl.musialowicz.contactlist.contact;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DataManagement {

    private final static DataManagement instance = new DataManagement();
    private static String nameOfTheFile;
    private final ObservableList<Contact> contacts;
    private int highlightedContactsCount;

    private DataManagement() {
        this.contacts = FXCollections.observableArrayList();
        nameOfTheFile = "database.txt";
        highlightedContactsCount = 0;
    }

    public ObservableList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ObservableList<Contact> contacts) {
        this.contacts.setAll(contacts);
    }

    public static DataManagement getInstance() {
        return instance;
    }

    public void createFile()  { // method which creates a database.txt file in case there isn't already one
        if(new File(nameOfTheFile).isFile()){
//            System.out.println("File exists.");
            return;
        }

        try {
            File file = new File(nameOfTheFile);
            file.createNewFile();
        } catch (IOException exception){
            System.out.println("Error: creating file failed..");
        }
    }

    public void loadContactsFromFile(){ // method which loads contacts from database.txt into the program when we start it.
        Path path = Paths.get(nameOfTheFile);
        String data;
        BufferedReader reader;

        try {
            reader = Files.newBufferedReader(path);
        } catch (IOException exception){
            System.out.println("Error - connection to file wasn't established.");
            return;
        }

        try {
            while ((data = reader.readLine()) != null){
                String[] dataArray = data.split(";;;");
                String phoneNumber = dataArray[0];
                String nickname = dataArray[1];
                String firstName = dataArray[2];
                String lastName = dataArray[3];
                String address = dataArray[4];
                boolean isImportant;
                if(dataArray[5].equals("true")){
                    isImportant = true;
                } else if (dataArray[5].equals("false")){
                    isImportant = false;
                } else {
                    isImportant = false;
                }
                contacts.add(new Contact(phoneNumber, nickname, firstName, lastName, address, isImportant));
            }
        } catch (IOException exception){
            System.out.println("Error - loading contacts to file failed.");
        } finally {
            try {
                reader.close();
            } catch (IOException exception){
                System.out.println("Error - closing connection to file failed.");
            }
        }
    }

    public void saveContactsToFile(){ // a method which saves contacts into .txt file when we stop the program.
        Path path = Paths.get(nameOfTheFile);
        Iterator<Contact> iterator = contacts.iterator();
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(path);
        } catch (IOException exception){
            System.out.println("Error - connection to file wasn't established.");
        }

        try {
            while (iterator.hasNext()) {
                Contact contact = iterator.next();
                String importance = contact.isImportant() ? "true" : "false";
                try {
                    writer.write(String.format("%s;;;%s;;;%s;;;%s;;;%s;;;%s",
                            contact.getPhoneNumber(),
                            contact.getNickname(),
                            contact.getFirstName(),
                            contact.getLastName(),
                            contact.getAddress(),
                            importance
                    ));
                    writer.newLine();
                } catch (IOException exception) {
                    System.out.println("Error - saving contacts to file failed.");
                }
            }
        } finally {
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException exception){
                    System.out.println("Error - connection to file couldn't be closed.");
                }
            }
        }
    }

    public int getHighlightedContactsCount() {
        return highlightedContactsCount;
    }

    public void setHighlightedContactsCount(int highlightedContactsCount) {
        this.highlightedContactsCount = highlightedContactsCount;
    }

    public void removeContact(Contact contact){
        if(contact.isImportant()){
            highlightedContactsCount--;
        }
        contacts.remove(contact);
    }
}
