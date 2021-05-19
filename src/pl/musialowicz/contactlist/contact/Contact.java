package pl.musialowicz.contactlist.contact;

public class Contact {

    private String phoneNumber;
    private String nickname;
    private String firstName;
    private String lastName;
    private String address;

    private boolean isImportant; // == isHighlighted

    public Contact(String phoneNumber, String nickname, String firstName, String lastName, String address, boolean isImportant) {
        this.phoneNumber = phoneNumber;
        this.nickname = nickname;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.isImportant = isImportant;
    }

    @Override
    public String toString() {
        return nickname;
    }

    public String getDetails(){ // details that are displayed in the text area of contact.
        StringBuilder text = new StringBuilder("\n\n\n");
        text.append("\tPHONE NUMBER: " + phoneNumber + "\n\n");
        text.append("\tFirst Name: " + firstName + "\n");
        text.append("\tLast Name: " + lastName + "\n");
        text.append("\tAddress: " + address + "\n");
        return text.toString();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getNickname() {
        return nickname;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }


    @Override
    public boolean equals(Object obj) {
        Contact contact = (Contact) obj;
        if(contact == null || this == null){
            return false;
        }
        if(contact.getNickname().equals(this.getNickname())){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return nickname.hashCode();
    }
}
