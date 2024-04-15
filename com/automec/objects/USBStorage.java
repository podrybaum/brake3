package com.automec.objects;

class USBStorage {
   public String path;
   public boolean hasDatabase = false;
   public boolean hasImages = false;

   public USBStorage(USB save) {
      this.path = save.path;
      this.hasDatabase = save.hasDatabase;
      this.hasImages = save.hasImages;
   }
}
