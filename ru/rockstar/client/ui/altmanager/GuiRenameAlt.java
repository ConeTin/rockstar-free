package ru.rockstar.client.ui.altmanager;

import java.awt.Color;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import ru.rockstar.api.utils.render.DrawHelper;

public class GuiRenameAlt extends GuiScreen {
   private final GuiAltManager manager;
   private GuiTextField nameField;
   private PasswordField pwField;
   private String status;

   public GuiRenameAlt(GuiAltManager manager) {
      this.status = TextFormatting.GRAY + "Waiting...";
      this.manager = manager;
   }

   public void actionPerformed(GuiButton button) {
      switch(button.id) {
      case 0:
         this.manager.selectedAlt.setMask(this.nameField.getText());
         this.manager.selectedAlt.setPassword(this.pwField.getText());
         this.status = "Edited!";
         break;
      case 1:
         this.mc.displayGuiScreen(this.manager);
      }

   }

   public void drawScreen(int par1, int par2, float par3) {
      new ScaledResolution(this.mc);
      DrawHelper.drawBorderedRect(0.0D, 0.0D, (double)this.width, (double)this.height, 0.5D, (new Color(17, 17, 17, 255)).getRGB(), (new Color(60, 60, 60, 255)).getRGB(), true);
    //  this.mc.fontRendererObj.drawStringWithShadow("Edit Alt", (double)((float)this.width / 2.0F), 10.0D, -1);
     // this.mc.fontRendererObj.drawStringWithShadow(this.status, (double)((float)this.width / 2.0F), 20.0D, -1);
      this.nameField.drawTextBox();
      this.pwField.drawTextBox();
      if (this.nameField.getText().isEmpty() && !this.nameField.isFocused()) {
         this.drawString(this.mc.fontRendererObj, "Name", this.width / 2 - 96, 66, -7829368);
      }

      if (this.pwField.getText().isEmpty() && !this.pwField.isFocused()) {
         this.drawString(this.mc.fontRendererObj, "Password", this.width / 2 - 96, 106, -7829368);
      }

      super.drawScreen(par1, par2, par3);
   }

   public void initGui() {
      this.buttonList.add(new GuiAltButton(0, this.width / 2 - 100, this.height / 4 + 92 + 12, "Edit"));
      this.buttonList.add(new GuiAltButton(1, this.width / 2 - 100, this.height / 4 + 116 + 12, "Cancel"));
      this.nameField = new GuiTextField(this.eventButton, this.mc.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
      this.pwField = new PasswordField(this.mc.fontRendererObj, this.width / 2 - 100, 100, 200, 20);
   }

   protected void keyTyped(char par1, int par2) {
      this.nameField.textboxKeyTyped(par1, par2);
      this.pwField.textboxKeyTyped(par1, par2);
      if (par1 == '\t' && (this.nameField.isFocused() || this.pwField.isFocused())) {
         this.nameField.setFocused(!this.nameField.isFocused());
         this.pwField.setFocused(!this.pwField.isFocused());
      }

      if (par1 == '\r') {
         this.actionPerformed((GuiButton)this.buttonList.get(0));
      }

   }

   protected void mouseClicked(int par1, int par2, int par3) {
      try {
         super.mouseClicked(par1, par2, par3);
      } catch (IOException var5) {
         var5.printStackTrace();
      }

      this.nameField.mouseClicked(par1, par2, par3);
      this.pwField.mouseClicked(par1, par2, par3);
   }
}
