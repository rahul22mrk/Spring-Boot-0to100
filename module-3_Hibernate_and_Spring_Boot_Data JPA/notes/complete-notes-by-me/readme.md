
## GitHub Mein Folder Banana

**Directly empty folder NAHI bana sakte** on GitHub (or Git). But indirectly **HAAN**, folder ban jata hai:

### Method 1: File create karo → Folder auto-ban jayega
GitHub pe "Add file" → "Create new file" click karo, aur path mein folder name likho:
```
my-new-folder/README.md
```
Ye `my-new-folder` folder automatically ban jayega aur usme `README.md` file aayegi.

### Method 2: Empty folder banana ho → `.gitkeep` trick
Git empty folders track nahi karta. Isliye ek hidden file rakh dete hain:
```
my-empty-folder/.gitkeep
```
`.gitkeep` ek convention hai (Git feature nahi) - ye folder ko Git mein visible banata hai.

### Summary:
| Kya | GitHub pe possible? | How? |
|---|---|---|
| New file | ✅ Yes | "Create new file" button |
| New folder (with file) | ✅ Yes | Path mein `folder/file.txt` likho |
| New empty folder | ❌ No direct way | `.gitkeep` file add karo |

Tumhare current project mein humne bhi same approach use kiya - `write_to_file` tool se `onetoone/User.java` create kiya toh `onetoone/` folder auto-ban gaya!
