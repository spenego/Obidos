package main

/*
 * Copyright (C) 2016 - 2026 Spenego Software LLC. All rights reserved.
 *
 * This file is part of Obidos from Spenego Software LLC
 *
 * Obidos is dual-licensed under a commercial license and the GNU
 * Affero General Public License (AGPL) v3.0. For commercial licensing,
 * contact Spenego Software LLC at https://spenego.com/contacts.html.
 *
 * For AGPL licensing terms, see the LICENSE file in the project root
 * or <https://www.gnu.org/licenses/>.
 */

//
// This program generates a random master password and a random paswword first then
// stretches the master password with PBKDF2 to generate master key and uses the
// master key to encrypt the random password.
// Spengo Obidos derive the key from master password and decrypt the key to
// make jdbc connection.
// Usage:
//
// The program will created jdbc.xml with encrypted password and jdbcm.xml with
// master password.
//
// The program prints the output on stdout like the following format:
//
// hostname:localhost|dbtype:mysql|dbname:obidos|username:obidos|password:2IZOXEjpEXOakVPIg4RDYUaS2NfAUu0auN3HhzJIXMQ=
//
// The password is in plaintext and the installer can use this to initialize database.
// At this time the crypto artificats are stored in disk but they can be stored
// in HSM in future.
//

import (
	"crypto/aes"
	"crypto/cipher"
	"crypto/rand"
	"crypto/sha256"
	"encoding/base64"
	"encoding/hex"
	"encoding/xml"
	"flag"
	"fmt"
	"os"
	"runtime"
	"strconv"
	"time"

	"golang.org/x/crypto/pbkdf2"
)

var (
	hostname         string
	port             int
	dbtype           string
	dbname           string
	username         string
	driverclass      string
	urlparams        string
	jdbcXMLFilePath  string
	jdbcmXMLFilePath string
	v                = false
	debug            = false
	dryrun           = false
)

const (
	progname                  = "GenObidosJdbc"
	version                   = "1.0.2"
	masterPassLen             = 16
	dbPassLen                 = 16
	xmlHeader                 = `<?xml version="1.0" encoding="UTF-8"?>` + "\n"
	hostnameDefault           = "127.0.0.1"
	portDefault               = 3306
	usernameDefault           = "obidos"
	dbnameDefault             = "obidos"
	dbtypeDefault             = "mariadb"
	driverclassDefault        = "org.mariadb.jdbc.Driver"
	urlparamsDefault          = "useUnicode=true&sslMode=trust&characterEncoding=UTF-8"
	automaticTestTableDefault = "c3p0Test"
	maxIdleTimeDefault        = "21555"
)

type jdbc struct {
	Hostname    string `xml:"hostname"`
	Port        int    `xml:"port"`
	DbType      string `xml:"dbtype"`
	DbName      string `xml:"dbname"`
	DriverClass string `xml:"driverClassName"`
	Username    string `xml:"username"`
	Password    string `xml:"password"`
	URL         string `xml:"url"`
	URLParams   string `xml:"urlparams"`
}

// Jdbcm ...
type jdbcm struct {
	Password string `xml:"password"`
}

func writeXML(file string, xml string) error {
	fd, err := os.Create(file)
	if err != nil {
		fatalError("ERROR: Could not write XML to file %s:%v\n", file, err.Error())
	}
	defer fd.Close()
	_, err = fd.WriteString(xml)
	if err != nil {
		return err
	}
	return nil
}

func thisYear() int {
	y, _, _ := time.Now().Date()
	return y
}

func usage() {
	fmt.Fprintf(os.Stderr, "%s v%s\n", progname, version)
	fmt.Fprintf(os.Stderr, "A program to generate Spenego Obidos jdbc information\n")
	fmt.Fprintf(os.Stderr, "Copyright %d Spenego Software LLC\n", thisYear())
	fmt.Fprintf(os.Stderr, "Usage: %s [flags]\n", progname)
	fmt.Fprintf(os.Stderr, "Where the flags are:\n")

	flag.PrintDefaults()
	fmt.Fprintf(os.Stderr, "\nNote: The default behavior is to generate a random password.\n")
	fmt.Fprintf(os.Stderr, "Use OBIDOS_DB_PASS env variable to specify a password\n\n")

}

func fatalError(format string, a ...interface{}) {
	fmt.Fprintf(os.Stderr, format, a...)
	os.Exit(1)
}
func logDebug(format string, a ...interface{}) {
	if debug {
		fmt.Fprintf(os.Stderr, format, a...)
	}
}

func validateFlags() {
}

// return has hex string
func genRandString(len int) (string, error) {
	r := make([]byte, len)
	_, err := rand.Read(r)
	if err != nil {
		fatalError("ERROR: could not generate %d byte random\n", len)
		return "", err
	}
	return hex.EncodeToString(r), nil
}

const algorithmNonceSize int = 12
const algorithmKeySize int = 16
const pBKDF2SaltSize int = 16
const pBKDF2Iterations int = 32767

// from SCEE
func encryptString(plaintext, password string) (string, error) {
	// Generate a 128-bit salt using a CSPRNG.
	salt := make([]byte, pBKDF2SaltSize)
	_, err := rand.Read(salt)
	if err != nil {
		return "", err
	}

	// Derive a key using PBKDF2.
	key := pbkdf2.Key([]byte(password), salt, pBKDF2Iterations, algorithmKeySize, sha256.New)

	// Encrypt and prepend salt.
	ciphertextAndNonce, err := encrypt([]byte(plaintext), key)
	if err != nil {
		return "", err
	}

	ciphertextAndNonceAndSalt := make([]byte, 0)
	ciphertextAndNonceAndSalt = append(ciphertextAndNonceAndSalt, salt...)
	ciphertextAndNonceAndSalt = append(ciphertextAndNonceAndSalt, ciphertextAndNonce...)

	// Return as base64 string.
	return base64.StdEncoding.EncodeToString(ciphertextAndNonceAndSalt), nil
}

func encrypt(plaintext, key []byte) ([]byte, error) {
	// Generate a 96-bit nonce using a CSPRNG.
	nonce := make([]byte, algorithmNonceSize)
	_, err := rand.Read(nonce)
	if err != nil {
		return nil, err
	}

	// Create the cipher and block.
	block, err := aes.NewCipher(key)
	if err != nil {
		return nil, err
	}

	cipher, err := cipher.NewGCM(block)
	if err != nil {
		return nil, err
	}

	// Encrypt and prepend nonce.
	ciphertext := cipher.Seal(nil, nonce, plaintext, nil)
	ciphertextAndNonce := make([]byte, 0)

	ciphertextAndNonce = append(ciphertextAndNonce, nonce...)
	ciphertextAndNonce = append(ciphertextAndNonce, ciphertext...)

	return ciphertextAndNonce, nil
}

func createJdbcXMLFile(encryptedDBPass string, filePath string) {
	v := &jdbc{}
	v.Hostname = hostname
	v.Port = port
	v.DbType = dbtype
	v.DbName = dbname
	v.Username = username
	v.Password = encryptedDBPass
	v.URLParams = urlparams
	v.DriverClass = driverclass
	// jdbc URL syntax for mysql is:
	// protocol//[hosts][/database][?properties]
	ports := strconv.FormatInt(int64(port), 10)
	url := "jdbc:" + dbtype + "://" + hostname + ":" + ports + "/" + dbname + "?" + urlparams
	v.URL = url

	output, err := xml.MarshalIndent(v, " ", "   ")
	if err != nil {
		fatalError("ERROR: Could not encode XML: %v\n", err.Error())
	}
	output = []byte(xml.Header + string(output))
	stringXML := string(output)
	if dryrun {
		fmt.Fprintf(os.Stderr, "JDBC file\n")
		fmt.Fprintf(os.Stderr, "=========\n")
		fmt.Fprintf(os.Stderr, "%v\n", stringXML)
	} else {
		writeXML(filePath, stringXML)
	}
}
func creatJdbcmXMLFile(masterPass string, filePath string) {
	v := &jdbcm{}
	v.Password = masterPass
	output, err := xml.MarshalIndent(v, " ", "   ")
	if err != nil {
		fatalError("ERROR: Could not encode master XML: %v\n", err.Error())
	}
	output = []byte(xml.Header + string(output))
	stringXML := string(output)
	if dryrun {
		fmt.Fprintf(os.Stderr, "JDBCM file\n")
		fmt.Fprintf(os.Stderr, "==========\n")
		fmt.Fprintf(os.Stderr, "%v\n", stringXML)
	} else {
		writeXML(filePath, stringXML)
	}
}

func main() {
	jdbcmXMLFilePathDefault := "/usr/local/spenego/obidos/jdbcm.xml"
	jdbcXMLFilePathDefault := "/usr/local/spenego/obidos/jdbc.xml"
	if runtime.GOOS == "windows" {
		jdbcXMLFilePathDefault = "c:/spenego/obidos/jdbc.xml"
		jdbcmXMLFilePathDefault = "c:/spenego/obidos/jdbcm.xml"
	}

	// -hostname
	flag.StringVar(&hostname, "hostname", hostnameDefault, "FQDN/IP address of database host")

	// -port
	flag.IntVar(&port, "port", portDefault, "Database port")

	// -dbname
	flag.StringVar(&dbname, "dbname", dbnameDefault, "Database name")

	// -dbType
	flag.StringVar(&dbtype, "dbtype", dbtypeDefault, "Database type")

	// -username
	flag.StringVar(&username, "username", usernameDefault, "Database username")

	// -driverclass
	flag.StringVar(&driverclass, "driverclass", driverclassDefault, "Database driver class")

	//-urlparams
	flag.StringVar(&urlparams, "urlparams", urlparamsDefault, "Default parameters for jdbc URL")

	// -debug
	flag.BoolVar(&debug, "debug", false, "Print debug messages to stderr")

	// -dryrun
	flag.BoolVar(&dryrun, "dryrun", false, "Do not try to write the XML files")

	// -jdbcpath
	flag.StringVar(&jdbcXMLFilePath, "jdbc", jdbcXMLFilePathDefault, "Patch of jdbc xml file")

	// -jdbcmpath
	flag.StringVar(&jdbcmXMLFilePath, "jdbcm", jdbcmXMLFilePathDefault, "Path of jdbc master password file")

	// -v
	flag.BoolVar(&v, "v", false, "Print version and exit")

	flag.Usage = func() {
		usage()
	}
	flag.Parse()

	if v {
		fmt.Fprintf(os.Stderr, "%s v%v\n", progname, version)
		os.Exit(0)
	}

	validateFlags()

	masterPass, err := genRandString(masterPassLen)
	if err != nil {
		fatalError(err.Error())
	}
	dbpassPlainText, ok := os.LookupEnv("OBIDOS_DB_PASS")
	if !ok {
		dbpassPlainText, err = genRandString(dbPassLen)
		if err != nil {
			fatalError(err.Error())
		}
	}

	// encrypt the dbpass with master key
	encryptedDBPass, err := encryptString(dbpassPlainText, masterPass)
	if err != nil {
		fatalError("ERROR: could not encrypt password: %v\n", err.Error())
	}
	logDebug("Master password: %v\n", masterPass)
	logDebug("dbPassCipher: %v\n", encryptedDBPass)

	createJdbcXMLFile(encryptedDBPass, jdbcXMLFilePath)
	creatJdbcmXMLFile(masterPass, jdbcmXMLFilePath)

	// go's stdout/stderr is not bufferred unlike C, no need for flushing etc.
	fmt.Printf("hostname:%v|dbtype:%v|port:%v|dbname:%v|username:%v|password:%v\n", hostname, dbtype, port, dbname, username, dbpassPlainText)

	os.Exit(0)
}
