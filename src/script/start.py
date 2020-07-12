import os
import subprocess
import requests


print('>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>')
print('>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>')

# The original code
with open('src/main/java/Original.java') as f:
    payload_0 = f.read()
r = requests.post('https://code2vec.ga/predict', data=payload_0)
result_origin = r.json()['0']['predictions']
print(result_origin)
count_equivalence = 0
count_error = 0

# The mutants
count = 0
for root, dirs, files in os.walk("./target/pit-reports/export/org/example/Original/mutants"):
    for name in dirs:
        file_name = os.path.join(root, name, "org.example.Original.class")
        print(file_name)
        mutant_file = os.path.join('./mutants', name)
        with open(mutant_file, 'w+') as out_file:
            subprocess.run(["java", "-jar", "cfr-0.150.jar", file_name], stdout=out_file)

        with open(mutant_file) as f:
            payload_1 = f.read()
        r = requests.post('https://code2vec.ga/predict', data=payload_1)
        if r.status_code == 200:
            result_mutant = r.json()['0']['predictions']
            if result_mutant[0]['name'] == result_origin[0]['name']:
                count_equivalence += 1
            print(result_mutant)
        else:
            count_error += 1

        count += 1

print('>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>')

print(count - count_equivalence, 'of', count, 'non-equivalent mutants have affected the prediction.')
print('Influence rate:', 1 - count_equivalence / (count - count_error))
print(count_error, 'mutants have caused the error.')

print('>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>')
print('>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>')

