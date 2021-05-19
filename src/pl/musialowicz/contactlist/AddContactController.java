package pl.musialowicz.contactlist;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pl.musialowicz.contactlist.contact.Contact;
import pl.musialowicz.contactlist.contact.DataManagement;

import java.util.List;

public class AddContactController {

    @FXML
    private TextField phoneNumberTextField;
    @FXML
    private TextField nicknameTextField;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private CheckBox importantContactCheckBox;
    @FXML
    private Label contactAddedOrNotLabel;
    
    public void addContact(){ // a method which adds contact into ListView.
        String phoneNumber, nickname, firstName, lastName, address;
        boolean isImportant;

        phoneNumber = phoneNumberTextField.getText();
        nickname = nicknameTextField.getText();
        firstName = firstNameTextField.getText();
        lastName = lastNameTextField.getText();
        address = addressTextField.getText();
        isImportant = importantContactCheckBox.isSelected();

        if(!isDataCorrect(phoneNumber, nickname, firstName, lastName, address)){ // checking whether data provided is correct.
            return;
        }

        DataManagement data = DataManagement.getInstance();
        List<Contact> contacts = data.getContacts();
        phoneNumber = phoneNumberFormatter(phoneNumber);

        if(lookForPhoneNumberOrNickname(contacts, phoneNumber, nickname) == 1){
            contactAddedOrNotLabel.setText("Phone number '" + phoneNumber + "' already exists in your contact list.");
            contactAddedOrNotLabel.setStyle("-fx-font-weight: bold");
            return;
        } else if (lookForPhoneNumberOrNickname(contacts, phoneNumber, nickname) == -1) {
            contactAddedOrNotLabel.setText("Nickname '" + nickname.trim() + "' already exists in your contact list.");
            contactAddedOrNotLabel.setStyle("-fx-font-weight: bold");
            return;
        } else {
            data.getContacts().add(new Contact(phoneNumber, nickname.trim(), firstName.trim(), lastName.trim(), address.trim(), isImportant));
            contactAddedOrNotLabel.setText("Contact added successfully.");
            contactAddedOrNotLabel.setStyle("-fx-font-weight: bold");
            if(isImportant){
                data.setHighlightedContactsCount(data.getHighlightedContactsCount() + 1);
                return;
            } else {
                return;
            }
        }
    }

    // a function which returns 1 - if there is a phone number found that belongs to someone else, -1 - if there is someone
    // with the same nickname (ignores extra spaces and lower/upper cases) and finally returns 0 - if everything is alright.
    public int lookForPhoneNumberOrNickname(List<Contact> contacts, String phoneNumber, String nickname){
        for(Contact contact: contacts){
            if(phoneNumber.equals(contact.getPhoneNumber())){
                return 1; // exists.
            }
            if(nickname.trim().toUpperCase().equals(contact.getNickname().toUpperCase().trim())){
                return -1;
            }
        }
        return 0;
    }


    // formats phone number from XXXXXXXXX to XXX-XXX-XXX.
    private String phoneNumberFormatter(String phoneNumber){
        char[] phoneNumberDigits = phoneNumber.toCharArray();
        StringBuilder transformedPhoneNumber = new StringBuilder();
        transformedPhoneNumber.append(phoneNumberDigits[0]);
        transformedPhoneNumber.append(phoneNumberDigits[1]);
        transformedPhoneNumber.append(phoneNumberDigits[2]);
        transformedPhoneNumber.append("-");
        transformedPhoneNumber.append(phoneNumberDigits[3]);
        transformedPhoneNumber.append(phoneNumberDigits[4]);
        transformedPhoneNumber.append(phoneNumberDigits[5]);
        transformedPhoneNumber.append("-");
        transformedPhoneNumber.append(phoneNumberDigits[6]);
        transformedPhoneNumber.append(phoneNumberDigits[7]);
        transformedPhoneNumber.append(phoneNumberDigits[8]);
        return transformedPhoneNumber.toString();
    }


    // checks whether data provided is correct.
    private boolean isDataCorrect(String phoneNumber, String nickname, String firstName, String lastName, String address){
        if(phoneNumber == null || phoneNumber.isEmpty()){
            contactAddedOrNotLabel.setText("Fill in all the data");
            contactAddedOrNotLabel.setStyle("-fx-font-weight: bold");
            return false;
        }
        if(nickname == null || nickname.isEmpty()){
            contactAddedOrNotLabel.setText("Fill in all the data");
            contactAddedOrNotLabel.setStyle("-fx-font-weight: bold");
            return false;
        }
        if(firstName == null || firstName.isEmpty()){
            contactAddedOrNotLabel.setText("Fill in all the data");
            contactAddedOrNotLabel.setStyle("-fx-font-weight: bold");
            return false;
        }
        if(lastName == null || lastName.isEmpty()){
            contactAddedOrNotLabel.setText("Fill in all the data");
            contactAddedOrNotLabel.setStyle("-fx-font-weight: bold");
            return false;
        }
        if(address == null || address.isEmpty()){
            contactAddedOrNotLabel.setText("Fill in all the data");
            contactAddedOrNotLabel.setStyle("-fx-font-weight: bold");
            return false;
        }
        if(!phoneNumber.chars().allMatch(Character::isDigit)){
            contactAddedOrNotLabel.setText("Write phone number in format: XXXXXXXXX");
            contactAddedOrNotLabel.setStyle("-fx-font-weight: bold");
            return false;
        }
        if(phoneNumber.length() != 9){
            contactAddedOrNotLabel.setText("Phone number must contain exactly 9 digits.");
            contactAddedOrNotLabel.setStyle("-fx-font-weight: bold");
            return false;
        }
        return true;
    }

}
