# Cipherx

## Overview

`Cipherx` is a comprehensive JavaFX-based application that showcases object-oriented programming concepts by providing a suite of cryptographic and data-hiding tools. It includes encryption/decryption algorithms, cryptographic hashing, metadata obfuscation, and steganography, all accessible through an intuitive graphical interface.

## Features

- **Encryption & Decryption**: AES, RSA, ChaCha20, ECC algorithms
- **Cryptographic Hashing**: MD2, MD5, SHA-1, SHA-224, SHA-256, SHA-384, SHA-512
- **Metadata Hiding**: Windows Alternate Data Streams (ADS) and Unix Extended Attributes (xattr)
- **Object-Oriented Design**: Demonstrates SOLID principles via distinct packages:
  - `com.godgamer.backend.Encryption`
  - `com.godgamer.backend.Cryptography`
  - `com.godgamer.backend.Handler`
  - `com.godgamer.backend.obfuscation`
  - `com.godgamer.frontend` (JavaFX UI controllers)

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 11 or higher
- Maven 3.6+ (for build automation)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Csral/OOPs-Project.git
   cd OOPs-Project/cipherx
   ```
2. **Build the project**
   ```bash
   mvn clean install
   ```
3. **Run the application**
   ```bash
   mvn javafx:run
   ```

## Usage

1. Launch `App.java` (the entry point) or use `mvn javafx:run`.
2. Navigate through the main scene to select:
   - Encryption / Decryption
   - Hashing (Cryptography)
   - Metadata Hiding (Obfuscation)
   - Steganography
3. Follow on-screen prompts to select files, input keys, or enter messages.

## Project Structure

```
cipherx/
├── src/
│   ├── main/
│   │   ├── java/com/godgamer/
│   │   │   ├── backend/
│   │   │   │   ├── Encryption/       # AES, RSA, ChaCha20, and test classes
│   │   │   │   ├── Cryptography/     # Hashing utility (Cryptographer.java)
│   │   │   │   ├── Handler/          # File read/write and block handling
│   │   │   │   └── obfuscation/      # Metadata hiding (Hiding.java)
│   │   │   └── frontend/             # JavaFX controllers and App launcher
│   │   └── resources/com/godgamer/frontend/
│   │       ├── Scenes/               # FXML layouts for each feature
│   │       ├── Styles/               # CSS files for light/dark themes
│   │       └── Images/               # Icons and assets
│   └── test/                         # (Optional) future unit tests
├── .vscode/                         # Editor settings
├── hi_private.key & hi_public.key   # Sample key files for demo
├── pom.xml                          # Maven build configuration
└── LICENSE                          # GNU GPL v3
```

## Contributing

Contributions are welcome! Please fork the repository, create a feature branch, and submit a pull request. Ensure your code follows the existing style and includes relevant tests.

## Authors

- Chaturya (Csral) <chaturyasral@gmail.com>
- Chirayu (Champion2049) <me.chirayu.6@gmail.com>
- Rtamanyu (God-Gamer-Manyu) <rtamanyu@gmail.com>
- Ekansh (Ekansh-K) <bl.ai.u4aid24075@bl.students.amrita.edu>

## License

This project is licensed under the **GNU General Public License v3.0**. See the [LICENSE](LICENSE) file for details.

