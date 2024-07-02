import requests
import os

GITLAB_URL = ''
GROUPS = ['']
ACCESS_TOKEN = ''
headers = {
    'Authorization': f'Bearer {ACCESS_TOKEN}'
}
for GROUP_NAME in GROUPS:
    api_url = f'{GITLAB_URL}/api/v4/groups/{GROUP_NAME}/projects'
    response = requests.get(api_url, headers=headers)
    project_infos = response.json()
    for project_info in project_infos:
        project_url = project_info['http_url_to_repo']
        project_name = project_info['name']
        project_dir = f'./{GROUP_NAME}/{project_name}'
        os.system(f'git clone --recursive {project_url} {project_dir}')


