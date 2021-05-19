package pl.musialowicz.contactlist;

import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.util.Callback;
import pl.musialowicz.contactlist.contact.Contact;
import pl.musialowicz.contactlist.contact.DataManagement;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Predicate;


public class Controller {

    @FXML
    private ListView<Contact> listViewOfContacts;

    @FXML
    private TextArea contactDetailsTextArea;

    @FXML
    private BorderPane mainWindow;

    @FXML
    private Label contactCountLabel;

    @FXML
    private Label importantContactCountLabel;

    @FXML
    private RadioButton displayAllRadioButton;

    @FXML
    private RadioButton displayHighlightedRadioButton;

    @FXML
    private RadioButton displayUnhighlightedRadioButton;

    private FilteredList<Contact> filteredList;

    private Predicate<Contact> displayAll, displayHighlighted, displayUnhighlighted;

    private ContextMenu listContextMenu;

    private final DataManagement dataInstance = DataManagement.getInstance();





    public void initialize() {
        listContextMenu = new ContextMenu();
        MenuItem deleteContact = new MenuItem("Delete contact.");
        MenuItem editContact = new MenuItem("Edit contact.");
        deleteContact.setOnAction(event -> {
            Contact contact = listViewOfContacts.getSelectionModel().getSelectedItem();
            removeContact(contact);
        });
        editContact.setOnAction(event -> {
            Contact contact = listViewOfContacts.getSelectionModel().getSelectedItem();
            try {
                editContact(contact);
            }
            catch (InterruptedException e){
                System.out.println("...");
            }
        });
        listContextMenu.getItems().add(deleteContact);
        listContextMenu.getItems().add(editContact);

        startWithSomething();
        cellHandler();
    }

    // creats Predicates, sorts list and calls text area listener.
    private void startWithSomething(){

        filterPredicates();
        sortList();
        textAreaHandler();
        contactCountLabel.setText("Number of contacts: " + dataInstance.getContacts().size());
        for(Contact contact : dataInstance.getContacts()){
            if(contact.isImportant()){
                dataInstance.setHighlightedContactsCount(dataInstance.getHighlightedContactsCount() + 1);
            }
        }
        importantContactCountLabel.setText("Number of highlighted contacts: " + dataInstance.getHighlightedContactsCount());
    }


    // sets Context Menu to cell in ListView and also makes cell bold when the Contact is set to important/highlighted.
    public void cellHandler(){
        listViewOfContacts.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Contact> call(ListView<Contact> contactListView) {
                ListCell<Contact> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(Contact contact, boolean empty) {
                        super.updateItem(contact, empty);
                        if (!empty && contact != null) {
                            setText(contact.getNickname());
                            if (contact.isImportant()) {
                                setStyle("-fx-font-weight: bold");
                            } else {
                                setStyle(null);
                            }
                        } else {
                            setText(null);
                        }
                    }
                };
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        });

                return cell;
            }
        });
    }


    // posistons text in textArea (contact's details), sets font, its size and listens for any changes.
    public void textAreaHandler(){
        listViewOfContacts.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                Contact tempContact1 = listViewOfContacts.getSelectionModel().getSelectedItem();
                contactDetailsTextArea.setText(tempContact1.getDetails());
                contactDetailsTextArea.setStyle("-fx-font-size: 18;");
                Node lText = contactDetailsTextArea.lookup(".text");
                lText.setStyle("-fx-text-alignment: center;");
                contactDetailsTextArea.setFont(Font.font ("Tahoma", 20));
            }
        });
    }


    // creates three predicates to filter items in list.
    public void filterPredicates(){
        displayAll = contact -> true;
        displayHighlighted = contact -> {
            if(contact.isImportant()){
                return true;
            } else {
                return false;
            }
        };
        displayUnhighlighted = contact -> {
            if(contact.isImportant()){
                return false;
            } else {
                return true;
            }
        };
    }


    @FXML
    public void removeContact(){
        Contact tempContact = listViewOfContacts.getSelectionModel().getSelectedItem();
        if(tempContact == null){
            return;
        }
        removeContact(tempContact);
    }


    // removes contact from the list view.
    public void removeContact(Contact contact){
       Alert removeAlert = new Alert(Alert.AlertType.CONFIRMATION);
       removeAlert.setTitle("Remove contact");
       removeAlert.setHeaderText("Removing " + contact.getNickname() + " from contact list.");
       removeAlert.setContentText("Press 'OK' to delete or 'CANCEL' to go back");
       Optional<ButtonType> result = removeAlert.showAndWait();
       if(result.isPresent() && result.get() == ButtonType.OK){
           dataInstance.removeContact(contact);
           int index = listViewOfContacts.getSelectionModel().getSelectedIndex();
           int range = listViewOfContacts.getItems().size();
           Contact prevOrNextContact;
           if(index - 1 >= 0){
               prevOrNextContact = listViewOfContacts.getItems().get(index);
               contactDetailsTextArea.setText(prevOrNextContact.getDetails());
           } else if (index + 1 < range){
               prevOrNextContact = listViewOfContacts.getItems().get(index);
               contactDetailsTextArea.setText(prevOrNextContact.getDetails());
           } else {
               contactDetailsTextArea.setText("");
           }
           contactCountLabel.setText("Number of contacts: " + dataInstance.getContacts().size());
           importantContactCountLabel.setText("Number of highlighted contacts: " + dataInstance.getHighlightedContactsCount());
       }
    }


    // adds new contact to list view.
    @FXML
    public void addNewContact(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainWindow.getScene().getWindow());
        FXMLLoader addContactDialogLoader = new FXMLLoader();
        addContactDialogLoader.setLocation(getClass().getResource("addContact.fxml"));
        dialog.setTitle("Add new contact");
        dialog.setHeaderText("Add new contact into the list.");
        try {
            dialog.getDialogPane().setContent(addContactDialogLoader.load());
        } catch (IOException exception){
            System.out.println("Error - add contact dialog didn't load.");
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.APPLY);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.APPLY){
           AddContactController controller = addContactDialogLoader.getController();
           controller.addContact();
           contactCountLabel.setText("Number of contacts: " + dataInstance.getContacts().size());
           importantContactCountLabel.setText("Number of highlighted contacts: " + dataInstance.getHighlightedContactsCount());
           listViewOfContacts.refresh();
        }
    }


    // edits existing contact in the list.
    @FXML
    public void editContact(Contact contact) throws InterruptedException{
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainWindow.getScene().getWindow());
        FXMLLoader editContactDialogLoader = new FXMLLoader();
        editContactDialogLoader.setLocation(getClass().getResource("editContact.fxml"));
        dialog.setTitle("Edit contact");
        dialog.setHeaderText("Editing contact: " + contact.getNickname());
        try {
            dialog.getDialogPane().setContent(editContactDialogLoader.load());
        } catch (IOException exception){
            System.out.println("Error - edit contact dialog didn't load.");
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.APPLY);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        editContactController controller = editContactDialogLoader.getController();
        controller.setFields(contact);
        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.APPLY){
            String errorMessage = controller.editContact(contact);
            if(!errorMessage.equals("")) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("ERROR");
                errorAlert.setContentText(errorMessage);
                errorAlert.show();
            }
            contactCountLabel.setText("Number of contacts: " + dataInstance.getContacts().size());
            importantContactCountLabel.setText("Number of highlighted contacts: " + dataInstance.getHighlightedContactsCount());
            listViewOfContacts.refresh();
        }

    }


    // sets contact to highlighted/important.
    @FXML
    public void setImportantToTrue(){
        Contact contact = listViewOfContacts.getSelectionModel().getSelectedItem();
        if(contact == null || contact.isImportant()){
            return;
        } else {
            contact.setImportant(true);
        }
        dataInstance.setHighlightedContactsCount(dataInstance.getHighlightedContactsCount() + 1);
        importantContactCountLabel.setText("Number of highlighted contacts: " + dataInstance.getHighlightedContactsCount());
        sortList();
        listViewOfContacts.refresh();
    }


    // sets contact to unimportant/unhighlighted.
    @FXML
    public void setImportantToFalse(){
        Contact contact = listViewOfContacts.getSelectionModel().getSelectedItem();
        if(contact == null || !contact.isImportant()){
            return;
        } else {
            contact.setImportant(false);
        }
        dataInstance.setHighlightedContactsCount(dataInstance.getHighlightedContactsCount() - 1);
        importantContactCountLabel.setText("Number of highlighted contacts: " + dataInstance.getHighlightedContactsCount());
        sortList();
        listViewOfContacts.refresh();
    }


    // deletes contact when we press "DELETE" on selected contact.
    @FXML
    public void deleteContactByKey(KeyEvent keyEvent){
        Contact contact = listViewOfContacts.getSelectionModel().getSelectedItem();
        if(contact == null){
            return;
        } else {
            if(keyEvent.getCode().equals(KeyCode.DELETE)){
                removeContact(contact);
            }
        }
    }



    // sorts list by importance (highlighted contacts in alphabetical order, then unhighlighted in alphabetical order).
    private void sortList(){
        filteredList = new FilteredList<>(dataInstance.getContacts(), displayAll);

        SortedList<Contact> sortedContacts = new SortedList<>(filteredList, (contact1, contact2) -> {
            if(!contact1.isImportant() && contact2.isImportant()){
                return 1;
            } else if (contact1.isImportant() && !contact2.isImportant()){
                return -1;
            } else {
                return contact1.getNickname().compareToIgnoreCase(contact2.getNickname());
            }
        });

        listViewOfContacts.setItems(sortedContacts);
        dataInstance.setContacts(sortedContacts);
    }


    // sets right predicate for the filter when we change selected radio button.
    @FXML
    public void filterListView(){
        Contact displayedContact = listViewOfContacts.getSelectionModel().getSelectedItem();
        if(displayAllRadioButton.isSelected()){
            filteredList.setPredicate(displayAll);
            if(displayedContact != null){
                listViewOfContacts.getSelectionModel().select(displayedContact);
            }
        } else if (displayHighlightedRadioButton.isSelected()){
            filteredList.setPredicate(displayHighlighted);
            if(displayedContact != null){
                if(!displayedContact.isImportant()){
                    listViewOfContacts.getSelectionModel().selectFirst();
                } else {
                    listViewOfContacts.getSelectionModel().select(displayedContact);
                }
            }
        } else if (displayUnhighlightedRadioButton.isSelected()){
            filteredList.setPredicate(displayUnhighlighted);
            if(displayedContact != null){
                if(displayedContact.isImportant()){
                    listViewOfContacts.getSelectionModel().selectFirst();
                } else {
                    listViewOfContacts.getSelectionModel().select(displayedContact);
                }
            }
        }
    }


    // exits the program.
    @FXML
    public void exitProgram(){
        Alert exitAlter = new Alert(Alert.AlertType.CONFIRMATION);
        exitAlter.setTitle("Quitting program.");
        exitAlter.setContentText("Are you sure you want to exit the program?");
        ((Button) exitAlter.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes, I do.");
        ((Button) exitAlter.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No, I want to go back.");
        Optional<ButtonType> result = exitAlter.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            Platform.exit();
        }
    }

}
