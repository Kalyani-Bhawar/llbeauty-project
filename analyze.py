import os
import re

def analyze_project(root_dir):
    controllers = []
    services = []
    repositories = []
    entities = []

    for subdir, dirs, files in os.walk(root_dir):
        for file in files:
            if file.endswith('.java'):
                path = os.path.join(subdir, file)
                if '\\controller\\' in path or '/controller/' in path:
                    controllers.append(path)
                elif '\\service\\' in path or '/service/' in path:
                    services.append(path)
                elif '\\repository\\' in path or '/repository/' in path:
                    repositories.append(path)
                elif '\\entity\\' in path or '/entity/' in path:
                    entities.append(path)

    report = []
    report.append("=== Architecture Audit Report ===")

    # 1. Controllers injecting Repositories
    report.append("\n1. Controllers directly injecting Repositories:")
    for c in controllers:
        with open(c, 'r', encoding='utf-8') as f:
            content = f.read()
            if re.search(r'(?:private\s+final\s+\w*Repository|@Autowired\s+private\s+\w*Repository)', content):
                report.append(" - " + os.path.basename(c))

    # 2. Business Logic inside Controllers
    report.append("\n2. Business logic or DB operations inside Controllers:")
    for c in controllers:
        with open(c, 'r', encoding='utf-8') as f:
            lines = f.readlines()
            for i, line in enumerate(lines):
                if re.search(r'\.(?:save|delete|findBy|count|exists|flush)\(', line) and not 'return' in line and not 'ResponseEntity' in line:
                    report.append(f" - {os.path.basename(c)}: DB operation at line {i+1}")
                if re.search(r'\b(?:for\s*\(|while\s*\()', line):
                    report.append(f" - {os.path.basename(c)}: Loop at line {i+1}")

    # 4. Oversized classes
    report.append("\n4. Oversized classes (> 300 lines):")
    for group in [controllers, services]:
        for c in group:
            with open(c, 'r', encoding='utf-8') as f:
                lines = f.readlines()
                if len(lines) > 300:
                    report.append(f" - {os.path.basename(c)} ({len(lines)} lines)")

    # 7. Entities with business logic
    report.append("\n7. Entities containing potential business logic:")
    for e in entities:
        with open(e, 'r', encoding='utf-8') as f:
            lines = f.readlines()
            methods = 0
            for line in lines:
                if re.search(r'public\s+\w+\s+\w+\s*\(', line) and not re.search(r'(?:get|set|equals|hashCode|builder)\w*', line, re.IGNORECASE):
                    methods += 1
            if methods > 3:
                report.append(f" - {os.path.basename(e)} has custom methods")

    with open('report.txt', 'w', encoding='utf-8') as f:
        f.write('\n'.join(report))

analyze_project(r'src/main/java/com/llbeauty')
