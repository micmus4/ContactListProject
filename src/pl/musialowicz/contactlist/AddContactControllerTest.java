package pl.musialowicz.contactlist;

import org.junit.Assert;
import org.junit.Test;
import pl.musialowicz.contactlist.contact.Contact;

import java.util.ArrayList;
import java.util.List;

public class AddContactControllerTest {

    @Test
    public void lookForPhoneNumberOrNicknameTest(){
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact("123-456-789", "Shepard", "John", "Shepard", "Normandy", true));
        AddContactController addContactController = new AddContactController();

        Assert.assertEquals(1, addContactController.lookForPhoneNumberOrNickname
                (contacts, "123-456-789", "Doesnt'matter")); // should return 1, same phone number.

        Assert.assertEquals(-1, addContactController.lookForPhoneNumberOrNickname
                (contacts, "333-333-333", "Shepard")); // should return -1, different phone number, but the same nickname.

        Assert.assertEquals(-1, addContactController.lookForPhoneNumberOrNickname
                (contacts, "333-333-333", "sHEPaRD")); // should return -1, different phone number,
                                                                           // the same nickname in different case.

        Assert.assertEquals(-1, addContactController.lookForPhoneNumberOrNickname
                (contacts, "333-333-333", "  Shepard   ")); //  should return -1, different phone number,
                                                                                // the same nickname but with extra spaces.

        Assert.assertEquals(0, addContactController.lookForPhoneNumberOrNickname
                (contacts, "333-333-333", "Different nickname")); //  should return 0, different phone number, different nickname.

    }

}