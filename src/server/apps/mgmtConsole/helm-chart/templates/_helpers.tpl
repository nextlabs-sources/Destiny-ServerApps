{{- define "administrator.name" -}}
{{- .Chart.Name }}
{{- end }}

{{- define "administrator.fullname" -}}
{{- printf "%s-%s" .Release.Name .Chart.Name }}
{{- end }}

{{- define "administrator.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version }}
{{- end }}

{{- define "administrator.labels" -}}
helm.sh/chart: {{ include "administrator.chart" . }}
{{ include "administrator.selectorLabels" . }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{- define "administrator.selectorLabels" -}}
app.kubernetes.io/name: {{ include "administrator.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}
