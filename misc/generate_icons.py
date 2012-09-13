import Image
import ImageDraw

pin = Image.new("RGBA", (256, 256))
dr = ImageDraw.Draw(pin)
dr.rectangle((184, 16, 215, 240), outline="#000", fill="#000")
#dr.rectangle((200, 16, 240, 240), outline="#ffffcc", fill="#ffffcc")
dr.rectangle((64, 120, 183, 135), outline="#000", fill="#000")

dr.rectangle((32, 88, 111, 103), outline="#0000ff", fill="#0000ff")
dr.rectangle((32, 88, 47, 167), outline="#0000ff", fill="#0000ff")
dr.rectangle((32, 152, 111, 167), outline="#0000ff", fill="#0000ff")
dr.rectangle((96, 88, 111, 167), outline="#0000ff", fill="#0000ff")

pin.save("res/PinTool-256x256.png")
pin.resize((64, 64), Image.NEAREST).save("res/PinTool-64x64.png")
pin.resize((32, 32), Image.NEAREST).save("res/PinTool-32x32.png")
pin.resize((16, 16), Image.NEAREST).save("res/PinTool-16x16.png")
