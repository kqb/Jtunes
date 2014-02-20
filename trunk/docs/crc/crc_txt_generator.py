TITLE_ROWS = 2
FILE_NAME = "syzygy CRC cards - Sheet1.tsv"
DELIMITER = '\t'
RESPONSIBILITY_DELIMITER = "."
COLLABORATOR_DELIMITER = " "
with open(FILE_NAME, 'r') as file:
    for i in range(TITLE_ROWS):
        file.readline()
    for line in file:
        entry_ = line.split(DELIMITER)
        [title, responsibility_, collaborator_] = entry_[:3]
        with open("%s.txt" % title, 'w') as output:
            output.write("Class name: %s\n" % title)
            output.write("Responsibilities:\n")
            for r in responsibility_.split(RESPONSIBILITY_DELIMITER):
                r_stripped = r.strip()
                if r_stripped:
                    output.write(" * %s\n" % r_stripped)
            output.write("Collaborators:\n")
            for c in collaborator_.split(COLLABORATOR_DELIMITER):
                c_stripped = c.strip()
                if c_stripped:
                    output.write(" * %s\n" % c_stripped)

