{{- define "installer.name" -}}
{{- .Chart.Name }}
{{- end }}

{{- define "installer.fullname" -}}
{{- printf "%s-%s" .Release.Name .Chart.Name }}
{{- end }}

{{- define "installer.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version }}
{{- end }}

{{- define "installer.labels" -}}
helm.sh/chart: {{ include "installer.chart" . }}
{{ include "installer.selectorLabels" . }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{- define "installer.selectorLabels" -}}
app.kubernetes.io/name: {{ include "installer.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}
