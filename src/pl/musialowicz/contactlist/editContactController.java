package pl.musialowicz.contactlist;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pl.musialowicz.contactlist.contact.Contact;
import pl.musialowicz.contactlist.contact.DataManagement;

import java.util.List;

public class editContactController {

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
    private Label editedAddedOrNotLabel;

    private boolean wasImportant;

    // sets contact's data into correct text fields.
    public void setFields(Contact contact){
        String phoneNumber, nickname, firstName, lastName, address;
        boolean isImportant;

        phoneNumber = phoneNumberFormatter(contact.getPhoneNumber(), true);
        nickname = contact.getNickname();
        firstName = contact.getFirstName();
        lastName = contact.getLastName();
        address = contact.getAddress();
        isImportant = contact.isImportant();

        phoneNumberTextField.setText(phoneNumber);
        nicknameTextField.setText(nickname);
        firstNameTextField.setText(firstName);
        lastNameTextField.setText(lastName);
        addressTextField.setText(address);
        importantContactCheckBox.setSelected(isImportant);
        wasImportant = isImportant;
    }

    // edits Contact, returns empty String if Contact was edited or returns message with error when it wasn't.
    public String editContact(Contact contact){
        String phoneNumber, nickname, firstName, lastName, address;
        boolean isImportant;

        phoneNumber = phoneNumberTextField.getText();
        nickname = nicknameTextField.getText();
        firstName = firstNameTextField.getText();
        lastName = lastNameTextField.getText();
        address = addressTextField.getText();
        isImportant = importantContactCheckBox.isSelected();

        if(!isDataCorrect(phoneNumber, nickname, firstName, lastName, address).equals("")){
            return isDataCorrect(phoneNumber, nickname, firstName, lastName, address);
        }

        DataManagement data = DataManagement.getInstance();
        List<Contact> contacts = data.getContacts();
        phoneNumber = phoneNumberFormatter(phoneNumber, false);

        if(lookForPhoneNumberOrNickname(contacts, phoneNumber, nickname, contact) == 1){
            return "Phone number '" + phoneNumber + "' already exists in your contact list.";
        } else if (lookForPhoneNumberOrNickname(contacts, phoneNumber, nickname, contact) == -1) {
            return "Nickname '" + nickname.trim() + "' already exists in your contact list.";
        } else {
            data.removeContact(contact);
            data.getContacts().add(new Contact(phoneNumber, nickname.trim(), firstName.trim(), lastName.trim(), address.trim(), isImportant));
            if(isImportant && !wasImportant){
                data.setHighlightedContactsCount(data.getHighlightedContactsCount() + 1);
                return "";
            } else if(isImportant && wasImportant){
                data.setHighlightedContactsCount(data.getHighlightedContactsCount() + 1);
                return "";
            } else {
                return "";
            }
        }
    }

    // 0 - when everything's okay, 1 - when phone number already exists in the list, -1 - if nickname does exist in the list.
    public int lookForPhoneNumberOrNickname(List<Contact> contacts, String phoneNumber, String nickname, Contact contactEdited){
        if (contactEdited.getPhoneNumber().equals(phoneNumber)
                || contactEdited.getNickname().trim().toUpperCase().equals(nickname.trim().toUpperCase())) {
            return 0;
        }
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


    // modified phoneNumberFormatter in comparasion to addContact's version.
    // if parameter 'way' is set to true, it formats the phone number from XXX-XXX-XXX to XXXXXXXXX
    // and when it is set to false, it formats it from XXXXXXXXX to XXX-XXX-XXX.
    private String phoneNumberFormatter(String phoneNumber, boolean way){
        if(way) {
            char[] phoneNumberDigits = phoneNumber.toCharArray();
            StringBuilder transformedPhoneNumber = new StringBuilder();
            for (int i = 0; i < phoneNumber.length(); i++) {
                boolean flag = Character.isDigit(phoneNumber.charAt(i));
                if (flag) {
                    transformedPhoneNumber.append(phoneNumberDigits[i]);
                }
            }
            return transformedPhoneNumber.toString();
        } else {
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
    }


    // checking whether the data is correct, returns empty String if everything is okay, returns message with error when it isn't.
    private String isDataCorrect(String phoneNumber, String nickname, String firstName, String lastName, String address){
        if(phoneNumber == null || phoneNumber.isEmpty()){
            return "Fill in all the data";
        }
        if(nickname == null || nickname.isEmpty()){
            return "Fill in all the data";
        }
        if(firstName == null || firstName.isEmpty()){
            return "Fill in all the data";
        }
        if(lastName == null || lastName.isEmpty()){
            return "Fill in all the data";
        }
        if(address == null || address.isEmpty()){
            return "Fill in all the data";
        }
        if(!phoneNumber.chars().allMatch(Character::isDigit)){
            return "Write phone number in format: XXXXXXXXX";
        }
        if(phoneNumber.length() != 9){
            return "Phone number must contain exactly 9 digits.";
        }
        return "";
    }

}
