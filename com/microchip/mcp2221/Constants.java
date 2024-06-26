package com.microchip.mcp2221;

public class Constants {
   public static final int E_NO_ERR = 0;
   public static final int E_ERR_UNKNOWN_ERROR = -1;
   public static final int E_ERR_CMD_FAILED = -2;
   public static final int E_ERR_INVALID_HANDLE = -3;
   public static final int E_ERR_INVALID_PARAMETER = -4;
   public static final int E_ERR_INVALID_PASS = -5;
   public static final int E_ERR_PASSWORD_LIMIT_REACHED = -6;
   public static final int E_ERR_FLASH_WRITE_PROTECTED = -7;
   public static final int E_ERR_NULL = -10;
   public static final int E_ERR_DESTINATION_TOO_SMALL = -11;
   public static final int E_ERR_INPUT_TOO_LARGE = -12;
   public static final int E_ERR_FLASH_WRITE_FAILED = -13;
   public static final int E_ERR_NO_SUCH_INDEX = -101;
   public static final int E_ERR_DEVICE_NOT_FOUND = -103;
   public static final int E_ERR_INTERNAL_BUFFER_TOO_SMALL = -104;
   public static final int E_ERR_OPEN_DEVICE_ERROR = -105;
   public static final int E_ERR_CONNECTION_ALREADY_OPENED = -106;
   public static final int E_ERR_CLOSE_FAILED = -107;
   public static final int E_ERR_RAW_TX_TOO_LARGE = -301;
   public static final int E_ERR_RAW_TX_COPYFAILED = -302;
   public static final int E_ERR_RAW_RX_COPYFAILED = -303;
   public static final int E_ERR_INVALID_SPEED = -401;
   public static final int E_ERR_SPEED_NOT_SET = -402;
   public static final int E_ERR_INVALID_BYTE_NUMBER = -403;
   public static final int E_ERR_INVALID_ADDRESS = -404;
   public static final int E_ERR_I2C_BUSY = -405;
   public static final int E_ERR_I2C_READ_ERROR = -406;
   public static final int E_ERR_ADDRESS_NACK = -407;
   public static final int E_ERR_I2C_TIMEOUT = -408;
   public static final int E_ERR_TOO_MANY_RX_BYTES = -409;
   public static final int E_ERR_COPY_RX_DATA_FAILED = -410;
   public static final int E_ERR_NO_EFFECT = -411;
   public static final int E_ERR_COPY_TX_DATA_FAILED = -412;
   public static final int E_ERR_INVALID_PEC = -413;
   public static final int E_ERR_BLOCK_SIZE_MISMATCH = -414;
   public static final int E_ERR_LOAD_DLL = -1000;
   public static final int E_ERR_FUNCTION_NOT_FOUND = -10000;
   public static final int FLASH_SETTINGS = 0;
   public static final int RUNTIME_SETTINGS = 1;
   public static final int NO_CHANGE = 255;
   public static final int MCP2221_GPFUNC_IO = 0;
   public static final int MCP2221_GP_SSPND = 1;
   public static final int MCP2221_GP_CLOCK_OUT = 1;
   public static final int MCP2221_GP_USBCFG = 1;
   public static final int MCP2221_GP_LED_I2C = 1;
   public static final int MCP2221_GP_LED_UART_RX = 2;
   public static final int MCP2221_GP_ADC = 2;
   public static final int MCP2221_GP_LED_UART_TX = 3;
   public static final int MCP2221_GP_DAC = 3;
   public static final int MCP2221_GP_IOC = 4;
   public static final int MCP2221_GPDIR_INPUT = 1;
   public static final int MCP2221_GPDIR_OUTPUT = 0;
   public static final int MCP2221_GPVAL_HIGH = 1;
   public static final int MCP2221_GPVAL_LOW = 0;
   public static final int INTERRUPT_NONE = 0;
   public static final int INTERRUPT_POSITIVE_EDGE = 1;
   public static final int INTERRUPT_NEGATIVE_EDGE = 2;
   public static final int INTERRUPT_BOTH_EDGES = 3;
   public static final int VREF_VDD = 0;
   public static final int VREF_1024V = 1;
   public static final int VREF_2048V = 2;
   public static final int VREF_4096V = 3;
   public static final int MCP2221_USB_BUS = 128;
   public static final int MCP2221_USB_SELF = 64;
   public static final int MCP2221_USB_REMOTE = 32;
   public static final int MCP2221_PASS_ENABLE = 1;
   public static final int MCP2221_PASS_DISABLE = 0;
   public static final int MCP2221_PASS_CHANGE = 255;
}
