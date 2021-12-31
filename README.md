## Jaes

Jaes is a simple Java CLI file encryption program

## Encrypt a file

```
$ java Jaes file.txt e
```

You will then be prompted for a secret key

## Decrypt a file

```
$ java Jaes file.txt d
```

You will be asked for the key. If the key is incorrect, the program will most likely throw an exception and the file 
will stay encrypted.

## Important note

Jaes has not been audited by a security professional. Use at your own risk