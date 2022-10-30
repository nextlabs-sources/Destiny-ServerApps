{{- define "reporter.name" -}}
{{- .Chart.Name }}
{{- end }}

{{- define "reporter.fullname" -}}
{{- printf "%s-%s" .Release.Name .Chart.Name }}
{{- end }}

{{- define "reporter.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version }}
{{- end }}

{{- define "reporter.labels" -}}
helm.sh/chart: {{ include "reporter.chart" . }}
{{ include "reporter.selectorLabels" . }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{- define "reporter.selectorLabels" -}}
app.kubernetes.io/name: {{ include "reporter.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}
