{{- define "config-service.name" -}}
{{- .Chart.Name }}
{{- end }}

{{- define "config-service.fullname" -}}
{{- printf "%s-%s" .Release.Name .Chart.Name }}
{{- end }}

{{- define "config-service.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version }}
{{- end }}

{{- define "config-service.labels" -}}
helm.sh/chart: {{ include "config-service.chart" . }}
{{ include "config-service.selectorLabels" . }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{- define "config-service.selectorLabels" -}}
app.kubernetes.io/name: {{ include "config-service.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}
