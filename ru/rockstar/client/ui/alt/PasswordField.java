package ru.rockstar.client.ui.alt;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatAllowedCharacters;
import ru.rockstar.api.utils.render.DrawHelper;

import org.lwjgl.opengl.GL11;

public class PasswordField extends Gui {
	private final FontRenderer fontRenderer;
	private final int xPos;
	private final int yPos;
	private final int width;
	private final int height;
	private String text = "";
	private int maxStringLength = 50;
	private int cursorCounter;
	private boolean enableBackgroundDrawing = true;
	private boolean canLoseFocus = true;
	public boolean isFocused = false;
	private final boolean isEnabled = true;
	private int i = 0;
	private int cursorPosition = 0;
	private int selectionEnd = 0;
	private int enabledColor = 0xE0E0E0;
	private final int disabledColor = 0x707070;
	private boolean b = true;

	public PasswordField(FontRenderer fontRendererObj, int par2, int par3, int par4, int par5) {
		this.fontRenderer = fontRendererObj;
		this.xPos = par2;
		this.yPos = par3;
		this.width = par4;
		this.height = par5;
	}

	public void updateCursorCounter() {
		++this.cursorCounter;
	}

	public void setText(String par1Str) {
		this.text = par1Str.length() > this.maxStringLength ? par1Str.substring(0, this.maxStringLength) : par1Str;
		this.setCursorPositionEnd();
	}

	public String getText() {
		String newtext = this.text.replaceAll(" ", "");
		return newtext;
	}

	public String getSelectedtext() {
		int var1 = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		int var2 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		return this.text.substring(var1, var2);
	}

	public void writeText(String par1Str) {
		int var8;
		String var2 = "";
		String var3 = ChatAllowedCharacters.filterAllowedCharacters(par1Str);
		int var4 = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		int var5 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		int var6 = this.maxStringLength - this.text.length() - var4 - this.selectionEnd;
		boolean var7 = false;
		if (this.text.length() > 0) {
			var2 = var2 + this.text.substring(0, var4);
		}
		if (var6 < var3.length()) {
			var2 = var2 + var3.substring(0, var6);
			var8 = var6;
		} else {
			var2 = var2 + var3;
			var8 = var3.length();
		}
		if (this.text.length() > 0 && var5 < this.text.length()) {
			var2 = var2 + this.text.substring(var5);
		}
		this.text = var2.replaceAll(" ", "");
		this.cursorPos(var4 - this.selectionEnd + var8);
	}

	public void func_73779_a(int par1) {
		if (this.text.length() != 0) {
			if (this.selectionEnd != this.cursorPosition) {
				this.writeText("");
			} else {
				this.deleteFromCursor(this.getNthWordFromCursor(par1) - this.cursorPosition);
			}
		}
	}

	public void deleteFromCursor(int par1) {
		if (this.text.length() != 0) {
			if (this.selectionEnd != this.cursorPosition) {
				this.writeText("");
			} else {
				boolean var2 = par1 < 0;
				int var3 = var2 ? this.cursorPosition + par1 : this.cursorPosition;
				int var4 = var2 ? this.cursorPosition : this.cursorPosition + par1;
				String var5 = "";
				if (var3 >= 0) {
					var5 = this.text.substring(0, var3);
				}
				if (var4 < this.text.length()) {
					var5 = var5 + this.text.substring(var4);
				}
				this.text = var5;
				if (var2) {
					this.cursorPos(par1);
				}
			}
		}
	}

	public int getNthWordFromCursor(int par1) {
		return this.getNthWordFromPos(par1, this.getCursorPosition());
	}

	public int getNthWordFromPos(int par1, int par2) {
		return this.type(par1, this.getCursorPosition(), true);
	}

	public int type(int par1, int par2, boolean par3) {
		int var4 = par2;
		boolean var5 = par1 < 0;
		int var6 = Math.abs(par1);
		for (int var7 = 0; var7 < var6; ++var7) {
			if (!var5) {
				int var8 = this.text.length();
				if ((var4 = this.text.indexOf(32, var4)) == -1) {
					var4 = var8;
					continue;
				}
				while (par3 && var4 < var8 && this.text.charAt(var4) == ' ') {
					++var4;
				}
				continue;
			}
			while (par3 && var4 > 0 && this.text.charAt(var4 - 1) == ' ') {
				--var4;
			}
			while (var4 > 0 && this.text.charAt(var4 - 1) != ' ') {
				--var4;
			}
		}
		return var4;
	}

	public void cursorPos(int par1) {
		this.setCursorPosition(this.selectionEnd + par1);
	}

	public void setCursorPosition(int par1) {
		this.cursorPosition = par1;
		int var2 = this.text.length();
		if (this.cursorPosition < 0) {
			this.cursorPosition = 0;
		}
		if (this.cursorPosition > var2) {
			this.cursorPosition = var2;
		}
		this.func_73800_i(this.cursorPosition);
	}

	public void setCursorPositionZero() {
		this.setCursorPosition(0);
	}

	public void setCursorPositionEnd() {
		this.setCursorPosition(this.text.length());
	}

	public boolean textboxKeyTyped(char par1, int par2) {
		if (!this.isEnabled || !this.isFocused) {
			return false;
		}
		switch (par1) {
		case '\u0001': {
			this.setCursorPositionEnd();
			this.func_73800_i(0);
			return true;
		}
		case '\u0003': {
			GuiScreen.setClipboardString(this.getSelectedtext());
			return true;
		}
		case '\u0016': {
			this.writeText(GuiScreen.getClipboardString());
			return true;
		}
		case '\u0018': {
			GuiScreen.setClipboardString(this.getSelectedtext());
			this.writeText("");
			return true;
		}
		}
		switch (par2) {
		case 14: {
			if (GuiScreen.isCtrlKeyDown()) {
				this.func_73779_a(-1);
			} else {
				this.deleteFromCursor(-1);
			}
			return true;
		}
		case 199: {
			if (GuiScreen.isShiftKeyDown()) {
				this.func_73800_i(0);
			} else {
				this.setCursorPositionZero();
			}
			return true;
		}
		case 203: {
			if (GuiScreen.isShiftKeyDown()) {
				if (GuiScreen.isCtrlKeyDown()) {
					this.func_73800_i(this.getNthWordFromPos(-1, this.getSelectionEnd()));
				} else {
					this.func_73800_i(this.getSelectionEnd() - 1);
				}
			} else if (GuiScreen.isCtrlKeyDown()) {
				this.setCursorPosition(this.getNthWordFromCursor(-1));
			} else {
				this.cursorPos(-1);
			}
			return true;
		}
		case 205: {
			if (GuiScreen.isShiftKeyDown()) {
				if (GuiScreen.isCtrlKeyDown()) {
					this.func_73800_i(this.getNthWordFromPos(1, this.getSelectionEnd()));
				} else {
					this.func_73800_i(this.getSelectionEnd() + 1);
				}
			} else if (GuiScreen.isCtrlKeyDown()) {
				this.setCursorPosition(this.getNthWordFromCursor(1));
			} else {
				this.cursorPos(1);
			}
			return true;
		}
		case 207: {
			if (GuiScreen.isShiftKeyDown()) {
				this.func_73800_i(this.text.length());
			} else {
				this.setCursorPositionEnd();
			}
			return true;
		}
		case 211: {
			if (GuiScreen.isCtrlKeyDown()) {
				this.func_73779_a(1);
			} else {
				this.deleteFromCursor(1);
			}
			return true;
		}
		}
		if (ChatAllowedCharacters.isAllowedCharacter(par1)) {
			this.writeText(Character.toString(par1));
			return true;
		}
		return false;
	}

	public void mouseClicked(int par1, int par2, int par3) {
		boolean var4;
		boolean bl = var4 = par1 >= this.xPos && par1 < this.xPos + this.width && par2 >= this.yPos
				&& par2 < this.yPos + this.height;
		if (this.canLoseFocus) {
			this.setFocused(this.isEnabled && var4);
		}
		if (this.isFocused && par3 == 0) {
			int var5 = par1 - this.xPos;
			if (this.enableBackgroundDrawing) {
				var5 -= 4;
			}
			String var6 = this.fontRenderer.trimStringToWidth(this.text.substring(this.i), this.getWidth());
			this.setCursorPosition(this.fontRenderer.trimStringToWidth(var6, var5).length() + this.i);
		}
	}

	public void drawTextBox() {
		if (this.func_73778_q()) {
			if (this.getEnableBackgroundDrawing()) {
				DrawHelper.drawNewRect(this.xPos - 1.5, this.yPos - 1, this.xPos + this.width + 1.5, this.yPos + this.height-1.5, new Color(0, 0, 0, 95).getRGB());

			}
			int var1 = this.isEnabled ? this.enabledColor : this.disabledColor;
			int var2 = this.cursorPosition - this.i;
			int var3 = this.selectionEnd - this.i;
			String var4 = this.fontRenderer.trimStringToWidth(this.text.substring(this.i), this.getWidth());
			boolean var5 = var2 >= 0 && var2 <= var4.length();
			boolean var6 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && var5;
			int var7 = this.enableBackgroundDrawing ? this.xPos + 4 : this.xPos;
			int var8 = this.enableBackgroundDrawing ? this.yPos + (this.height - 8) / 2 : this.yPos;
			int var9 = var7;
			if (var3 > var4.length()) {
				var3 = var4.length();
			}
			if (var4.length() > 0) {
				if (var5) {
					var4.substring(0, var2);
				}
				var9 = Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(this.text.replaceAll("(?s).", "*"),
						var7, var8, var1);
			}
			boolean var10 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
			int var11 = var9;
			if (!var5) {
				var11 = var2 > 0 ? var7 + this.width : var7;
			} else if (var10) {
				var11 = var9 - 1;
				--var9;
			}
			if (var4.length() > 0 && var5 && var2 < var4.length()) {
				Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(var4.substring(var2), var9, var8, var1);
			}
			if (var6) {
				if (var10) {
					Gui.drawRect(var11, var8 - 1, var11 + 1, var8 + 1 + Minecraft.getMinecraft().neverlose500_16.getFontHeight(), -3092272);
				} else {
					Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("_", var11, var8, var1);
				}
			}
			if (var3 != var2) {
				int var12 = var7 + this.fontRenderer.getStringWidth(var4.substring(0, var3));
				this.drawCursorVertical(var11, var8 - 1, var12 - 1, var8 + 1 + Minecraft.getMinecraft().neverlose500_16.getFontHeight());
			}
		}
	}

	private void drawCursorVertical(int par1, int par2, int par3, int par4) {
		int var5;
		if (par1 < par3) {
			var5 = par1;
			par1 = par3;
			par3 = var5;
		}
		if (par2 < par4) {
			var5 = par2;
			par2 = par4;
			par4 = var5;
		}
		Tessellator var6 = Tessellator.getInstance();
		BufferBuilder var7 = var6.getBuffer();
		GL11.glColor4f(0.0f, 0.0f, 255.0f, 255.0f);
		GL11.glDisable(3553);
		GL11.glEnable(3058);
		GL11.glLogicOp(5387);
		var7.begin(7, var7.getVertexFormat());
		var7.pos(par1, par4, 0.0);
		var7.pos(par3, par4, 0.0);
		var7.pos(par3, par2, 0.0);
		var7.pos(par1, par2, 0.0);
		var7.finishDrawing();
		GL11.glDisable(3058);
		GL11.glEnable(3553);
	}

	public void setMaxStringLength(int par1) {
		this.maxStringLength = par1;
		if (this.text.length() > par1) {
			this.text = this.text.substring(0, par1);
		}
	}

	public int getMaxStringLength() {
		return this.maxStringLength;
	}

	public int getCursorPosition() {
		return this.cursorPosition;
	}

	public boolean getEnableBackgroundDrawing() {
		return this.enableBackgroundDrawing;
	}

	public void setEnableBackgroundDrawing(boolean par1) {
		this.enableBackgroundDrawing = par1;
	}

	public void func_73794_g(int par1) {
		this.enabledColor = par1;
	}

	public void setFocused(boolean par1) {
		if (par1 && !this.isFocused) {
			this.cursorCounter = 0;
		}
		this.isFocused = par1;
	}

	public boolean isFocused() {
		return this.isFocused;
	}

	public int getSelectionEnd() {
		return this.selectionEnd;
	}

	public int getWidth() {
		return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
	}

	public void func_73800_i(int par1) {
		int var2 = this.text.length();
		if (par1 > var2) {
			par1 = var2;
		}
		if (par1 < 0) {
			par1 = 0;
		}
		this.selectionEnd = par1;
		if (this.fontRenderer != null) {
			if (this.i > var2) {
				this.i = var2;
			}
			int var3 = this.getWidth();
			String var4 = this.fontRenderer.trimStringToWidth(this.text.substring(this.i), var3);
			int var5 = var4.length() + this.i;
			if (par1 == this.i) {
				this.i -= this.fontRenderer.trimStringToWidth(this.text, var3, true).length();
			}
			if (par1 > var5) {
				this.i += par1 - var5;
			} else if (par1 <= this.i) {
				this.i -= this.i - par1;
			}
			if (this.i < 0) {
				this.i = 0;
			}
			if (this.i > var2) {
				this.i = var2;
			}
		}
	}

	public void setCanLoseFocus(boolean par1) {
		this.canLoseFocus = par1;
	}

	public boolean func_73778_q() {
		return this.b;
	}

	public void func_73790_e(boolean par1) {
		this.b = par1;
	}
}
