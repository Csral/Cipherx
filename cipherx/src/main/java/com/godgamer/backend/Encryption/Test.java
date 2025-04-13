package com.godgamer.backend.Encryption;

public class Test {
 
    public static void main(String[] args) {
        
        try {

            AES test = new AES();
            test.MODIFY_IVSPACE();
            test.MODIFY_KEY();

            String m = test.encrypt_CTR("Hello!");
            System.out.println("Data: Hello nig!\nEncrypted: "+ m);

            String decoded = test.decrypt_CTR(m);

            System.out.println("Decoded: "+decoded);

            String password = "pzSY$:C]&e";
            
            String svfile = test.KEY_SAVE_SECURE(true, "pas", 2, true);

            // String svfile = test.KEY_SAVE(true, "paedwfkghwr4uohfwsruikowhpoia");

//            String svfile = test.KEY_SAVE();

            test.MODIFY_IVSPACE();
            test.MODIFY_KEY();

            decoded = test.decrypt_CTR(m);

            System.out.println("Decoded: "+decoded);

            // test.KEY_LOAD(svfile, true, "x3'6X{M05)1");

            decoded = test.decrypt_CTR(m);

            System.out.println("Decoded: "+decoded);

            //test.KEY_LOAD(svfile, true, "paedwfkghwr4uohfwsruikowhpoia");
            test.KEY_LOAD(svfile, true, "pas");
            //test.KEY_LOAD(svfile);

            decoded = test.decrypt_CTR(m);

            System.out.println("Decoded: "+decoded);

        } catch (Exception e) { 
            System.out.println("Something went wrong: " + e);
        }

    }

}